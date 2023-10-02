package io.angularpay.crypto.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.crypto.adapters.outbound.MongoAdapter;
import io.angularpay.crypto.adapters.outbound.RedisAdapter;
import io.angularpay.crypto.domain.CryptoRequest;
import io.angularpay.crypto.domain.InvestmentStatus;
import io.angularpay.crypto.domain.InvestmentTransactionStatus;
import io.angularpay.crypto.domain.Role;
import io.angularpay.crypto.exceptions.CommandException;
import io.angularpay.crypto.exceptions.ErrorObject;
import io.angularpay.crypto.helpers.CommandHelper;
import io.angularpay.crypto.models.GenericCommandResponse;
import io.angularpay.crypto.models.MakePaymentCommandRequest;
import io.angularpay.crypto.models.ResourceReferenceResponse;
import io.angularpay.crypto.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import static io.angularpay.crypto.exceptions.ErrorCode.REQUEST_COMPLETED_ERROR;
import static io.angularpay.crypto.exceptions.ErrorCode.REQUEST_REMOVED_ERROR;
import static io.angularpay.crypto.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.crypto.helpers.CommandHelper.validRequestStatusAndInvestmentExists;

@Service
public class MakePaymentCommand extends AbstractCommand<MakePaymentCommandRequest, GenericCommandResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse>,
        ResourceReferenceCommand<GenericCommandResponse, ResourceReferenceResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;

    public MakePaymentCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator, CommandHelper commandHelper, RedisAdapter redisAdapter) {
        super("MakePaymentCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
    }

    @Override
    protected String getResourceOwner(MakePaymentCommandRequest request) {
        return this.commandHelper.getInvestmentOwner(request.getRequestReference(), request.getInvestmentReference());
    }

    @Override
    protected GenericCommandResponse handle(MakePaymentCommandRequest request) {
        CryptoRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        String investmentReference = request.getInvestmentReference();
        validRequestStatusAndInvestmentExists(found, investmentReference);
        Supplier<GenericCommandResponse> supplier = () -> makePayment(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse makePayment(MakePaymentCommandRequest request) {
        CryptoRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        String transactionReference = UUID.randomUUID().toString();
        found.getInvestors().forEach(x -> {
            if (request.getInvestmentReference().equalsIgnoreCase(x.getReference())) {
                if (x.isDeleted()) {
                    throw CommandException.builder()
                            .status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .errorCode(REQUEST_REMOVED_ERROR)
                            .message(REQUEST_REMOVED_ERROR.getDefaultMessage())
                            .build();
                }
                if (Objects.nonNull(x.getInvestmentStatus()) && x.getInvestmentStatus().getStatus() == InvestmentTransactionStatus.SUCCESSFUL) {
                    throw CommandException.builder()
                            .status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .errorCode(REQUEST_COMPLETED_ERROR)
                            .message(REQUEST_COMPLETED_ERROR.getDefaultMessage())
                            .build();
                }
                if (Objects.isNull(x.getInvestmentStatus())) {
                    x.setInvestmentStatus(InvestmentStatus.builder().status(InvestmentTransactionStatus.PENDING).build());
                }
                // TODO: integrate with transaction service
                //  all of these details should come from transaction service
                x.getInvestmentStatus().setTransactionReference(transactionReference);
                x.getInvestmentStatus().setTransactionDatetime(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
                x.getInvestmentStatus().setStatus(InvestmentTransactionStatus.SUCCESSFUL);
            }
        });
        CryptoRequest response = this.mongoAdapter.updateRequest(found);
        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .itemReference(transactionReference)
                .cryptoRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(MakePaymentCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }

    @Override
    public String convertToUpdatesMessage(CryptoRequest cryptoRequest) throws JsonProcessingException {
        return this.commandHelper.toJsonString(cryptoRequest);
    }

    @Override
    public ResourceReferenceResponse map(GenericCommandResponse genericCommandResponse) {
        return new ResourceReferenceResponse(genericCommandResponse.getItemReference());
    }

    @Override
    public RedisAdapter getRedisAdapter() {
        return this.redisAdapter;
    }
}

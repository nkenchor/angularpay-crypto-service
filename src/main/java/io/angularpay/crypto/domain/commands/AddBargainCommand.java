package io.angularpay.crypto.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.crypto.adapters.outbound.MongoAdapter;
import io.angularpay.crypto.adapters.outbound.RedisAdapter;
import io.angularpay.crypto.domain.*;
import io.angularpay.crypto.exceptions.ErrorObject;
import io.angularpay.crypto.helpers.CommandHelper;
import io.angularpay.crypto.models.*;
import io.angularpay.crypto.validation.DefaultConstraintValidator;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import static io.angularpay.crypto.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.crypto.helpers.CommandHelper.validRequestStatusOrThrow;
import static io.angularpay.crypto.helpers.Helper.getAllPartiesExceptActor;
import static io.angularpay.crypto.models.UserNotificationType.INVESTOR_BARGAIN_ADDED;

@Service
public class AddBargainCommand extends AbstractCommand<AddBargainCommandRequest, GenericCommandResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse>,
        ResourceReferenceCommand<GenericCommandResponse, ResourceReferenceResponse>,
        UserNotificationsPublisherCommand<GenericCommandResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;

    public AddBargainCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator, CommandHelper commandHelper, RedisAdapter redisAdapter) {
        super("AddBargainCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
    }

    @Override
    protected String getResourceOwner(AddBargainCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected GenericCommandResponse handle(AddBargainCommandRequest request) {
        CryptoRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        validRequestStatusOrThrow(found);
        Supplier<GenericCommandResponse> supplier = () -> addBargain(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse addBargain(AddBargainCommandRequest request) throws OptimisticLockingFailureException {
        CryptoRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        if (Objects.isNull(found.getBargain())) {
            found.setBargain(Bargain.builder().build());
        }
        Offer offer = Offer.builder()
                .exchangeRate(ExchangeRate.builder()
                        .date(request.getAddBargainApiModel().getDate())
                        .from(request.getAddBargainApiModel().getFrom())
                        .rate(request.getAddBargainApiModel().getRate())
                        .to(request.getAddBargainApiModel().getTo())
                        .type(request.getAddBargainApiModel().getType())
                        .build())
                .comment(request.getAddBargainApiModel().getComment())
                .reference(UUID.randomUUID().toString())
                .userReference(request.getAuthenticatedUser().getUserReference())
                .createdOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .status(OfferStatus.PENDING)
                .build();
        Bargain bargain = found.getBargain();
        CryptoRequest response = this.commandHelper.addItemToCollection(found, offer, bargain::getOffers, bargain::setOffers);
        return GenericCommandResponse.builder()
                .requestReference(found.getReference())
                .itemReference(offer.getReference())
                .cryptoRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(AddBargainCommandRequest request) {
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

    @Override
    public UserNotificationType getUserNotificationType(GenericCommandResponse commandResponse) {
        return INVESTOR_BARGAIN_ADDED;
    }

    @Override
    public List<String> getAudience(GenericCommandResponse commandResponse) {
        return getAllPartiesExceptActor(commandResponse.getCryptoRequest(), commandResponse.getItemReference());
    }

    @Override
    public String convertToUserNotificationsMessage(UserNotificationBuilderParameters<GenericCommandResponse, CryptoRequest> parameters) throws JsonProcessingException {
        String summary;
        if (parameters.getCommandResponse().getCryptoRequest().getInvestee().getUserReference()
                .equalsIgnoreCase(parameters.getUserReference())) {
            summary = "someone made a bargain on your crypto post";
        } else {
            summary = "someone else made a bargain on a crypto post that you commented on";
        }

        UserNotificationBargainPayload userNotificationInvestmentPayload = UserNotificationBargainPayload.builder()
                .requestReference(parameters.getCommandResponse().getRequestReference())
                .bargainReference(parameters.getCommandResponse().getItemReference())
                .build();
        String payload = mapper.writeValueAsString(userNotificationInvestmentPayload);

        String attributes = mapper.writeValueAsString(parameters.getRequest());

        UserNotification userNotification = UserNotification.builder()
                .reference(UUID.randomUUID().toString())
                .createdOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .serviceCode(parameters.getRequest().getServiceCode())
                .userReference(parameters.getUserReference())
                .type(parameters.getType())
                .summary(summary)
                .payload(payload)
                .attributes(attributes)
                .build();

        return mapper.writeValueAsString(userNotification);
    }
}

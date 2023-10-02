package io.angularpay.crypto.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.crypto.adapters.outbound.MongoAdapter;
import io.angularpay.crypto.configurations.AngularPayConfiguration;
import io.angularpay.crypto.domain.CryptoRequest;
import io.angularpay.crypto.domain.Investor;
import io.angularpay.crypto.domain.Offer;
import io.angularpay.crypto.domain.RequestStatus;
import io.angularpay.crypto.exceptions.CommandException;
import io.angularpay.crypto.exceptions.ErrorCode;
import io.angularpay.crypto.models.GenericCommandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.angularpay.crypto.domain.InvestmentTransactionStatus.SUCCESSFUL;
import static io.angularpay.crypto.exceptions.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommandHelper {

    private final MongoAdapter mongoAdapter;
    private final ObjectMapper mapper;
    private final AngularPayConfiguration configuration;

    public GenericCommandResponse executeAcid(Supplier<GenericCommandResponse> supplier) {
        int maxRetry = this.configuration.getMaxUpdateRetry();
        OptimisticLockingFailureException optimisticLockingFailureException;
        int counter = 0;
        //noinspection ConstantConditions
        do {
            try {
                return supplier.get();
            } catch (OptimisticLockingFailureException exception) {
                if (counter++ >= maxRetry) throw exception;
                optimisticLockingFailureException = exception;
            }
        }
        while (Objects.nonNull(optimisticLockingFailureException));
        throw optimisticLockingFailureException;
    }

    public String getRequestOwner(String requestReference) {
        CryptoRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        return found.getInvestee().getUserReference();
    }

    private static CommandException commandException(HttpStatus status, ErrorCode errorCode) {
        return CommandException.builder()
                .status(status)
                .errorCode(errorCode)
                .message(errorCode.getDefaultMessage())
                .build();
    }

    public String getInvestmentOwner(String requestReference, String investmentReference) {
        CryptoRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        if (CollectionUtils.isEmpty(found.getInvestors())) return "";
        return found.getInvestors().stream()
                .filter(x -> investmentReference.equalsIgnoreCase(x.getReference()))
                .map(Investor::getUserReference)
                .findFirst()
                .orElse("");
    }

    public String getBargainOwner(String requestReference, String bargainReference) {
        CryptoRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        if (Objects.isNull(found.getBargain()) || CollectionUtils.isEmpty(found.getBargain().getOffers())) return "";
        return found.getBargain().getOffers().stream()
                .filter(x -> bargainReference.equalsIgnoreCase(x.getReference()))
                .map(Offer::getUserReference)
                .findFirst()
                .orElse("");
    }

    public <T> CryptoRequest updateProperty(CryptoRequest cryptoRequest, Supplier<T> getter, Consumer<T> setter) {
        setter.accept(getter.get());
        return this.mongoAdapter.updateRequest(cryptoRequest);
    }

    public <T> CryptoRequest addItemToCollection(CryptoRequest cryptoRequest, T newProperty, Supplier<List<T>> collectionGetter, Consumer<List<T>> collectionSetter) {
        if (CollectionUtils.isEmpty(collectionGetter.get())) {
            collectionSetter.accept(new ArrayList<>());
        }
        collectionGetter.get().add(newProperty);
        return this.mongoAdapter.updateRequest(cryptoRequest);
    }

    public <T> String toJsonString(T t) throws JsonProcessingException {
        return this.mapper.writeValueAsString(t);
    }

    public static CryptoRequest getRequestByReferenceOrThrow(MongoAdapter mongoAdapter, String requestReference) {
        return mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
    }

    public static void validRequestStatusAndBargainExists(CryptoRequest found, String bargainReference) {
        validRequestStatusOrThrow(found);
        if (Objects.isNull(found.getBargain()) || CollectionUtils.isEmpty(found.getBargain().getOffers())) {
            throw commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND);
        }
        if (found.getBargain().getOffers().stream().noneMatch(x -> bargainReference.equalsIgnoreCase(x.getReference()))) {
            throw commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND);
        }
    }

    public static void validRequestStatusAndInvestmentExists(CryptoRequest found, String investmentReference) {
        validRequestStatusOrThrow(found);
        if (CollectionUtils.isEmpty(found.getInvestors())) {
            throw commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND);
        }
        if (found.getInvestors().stream().noneMatch(x -> investmentReference.equalsIgnoreCase(x.getReference()))) {
            throw commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND);
        }
    }

    public static void validRequestStatusOrThrow(CryptoRequest found) {
        if (found.getStatus() == RequestStatus.COMPLETED) {
            throw commandException(HttpStatus.UNPROCESSABLE_ENTITY, REQUEST_COMPLETED_ERROR);
        }
        if (found.getStatus() == RequestStatus.CANCELLED) {
            throw commandException(HttpStatus.UNPROCESSABLE_ENTITY, REQUEST_CANCELLED_ERROR);
        }
    }

    public static void validateInvestmentStatusOrThrow(Investor investor) {
        if (Objects.nonNull(investor.getInvestmentStatus()) && investor.getInvestmentStatus().getStatus() == SUCCESSFUL) {
            throw commandException(HttpStatus.UNPROCESSABLE_ENTITY, REQUEST_COMPLETED_ERROR);
        }
    }
}

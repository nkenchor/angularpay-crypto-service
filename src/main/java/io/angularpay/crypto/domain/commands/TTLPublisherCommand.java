package io.angularpay.crypto.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.angularpay.crypto.adapters.outbound.RedisAdapter;
import io.angularpay.crypto.domain.CryptoRequest;

import java.util.Objects;

public interface TTLPublisherCommand<T extends CryptoRequestSupplier> {

    RedisAdapter getRedisAdapter();

    String convertToTTLMessage(CryptoRequest cryptoRequest, T t) throws JsonProcessingException;

    default void publishTTL(T t) {
        CryptoRequest cryptoRequest = t.getCryptoRequest();
        RedisAdapter redisAdapter = this.getRedisAdapter();
        if (Objects.nonNull(cryptoRequest) && Objects.nonNull(redisAdapter)) {
            try {
                String message = this.convertToTTLMessage(cryptoRequest, t);
                redisAdapter.publishTTL(message);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}

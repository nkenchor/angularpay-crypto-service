package io.angularpay.crypto.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.angularpay.crypto.adapters.outbound.RedisAdapter;
import io.angularpay.crypto.domain.CryptoRequest;

import java.util.Objects;

public interface UpdatesPublisherCommand<T extends CryptoRequestSupplier> {

    RedisAdapter getRedisAdapter();

    String convertToUpdatesMessage(CryptoRequest cryptoRequest) throws JsonProcessingException;

    default void publishUpdates(T t) {
        CryptoRequest cryptoRequest = t.getCryptoRequest();
        RedisAdapter redisAdapter = this.getRedisAdapter();
        if (Objects.nonNull(cryptoRequest) && Objects.nonNull(redisAdapter)) {
            try {
                String message = this.convertToUpdatesMessage(cryptoRequest);
                redisAdapter.publishUpdates(message);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}

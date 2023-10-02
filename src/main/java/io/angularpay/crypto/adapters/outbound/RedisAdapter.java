package io.angularpay.crypto.adapters.outbound;

import io.angularpay.crypto.ports.outbound.OutboundMessagingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisAdapter implements OutboundMessagingPort {

    private final RedisTopicPublisher redisTopicPublisher;
    private final RedisHashClient redisHashClient;

    @Override
    public void publishUpdates(String message) {
        this.redisTopicPublisher.publishUpdates(message);
    }

    @Override
    public void publishTTL(String message) {
        this.redisTopicPublisher.publishTTL(message);
    }

    @Override
    public void publishUserNotification(String message) {
        this.redisTopicPublisher.publishUserNotification(message);
    }

    @Override
    public Map<String, String> getPlatformConfigurations(String hashName) {
        return this.redisHashClient.getPlatformConfigurations(hashName);
    }
}

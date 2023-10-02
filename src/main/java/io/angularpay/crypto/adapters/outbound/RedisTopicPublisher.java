package io.angularpay.crypto.adapters.outbound;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTopicPublisher {

    private final StringRedisTemplate template;
    private final ChannelTopic updatesTopic;
    private final ChannelTopic ttlTopic;
    private final ChannelTopic userNotificationsTopic;

    public void publishUpdates(String message) {
        template.convertAndSend(updatesTopic.getTopic(), message);
    }

    public void publishTTL(String message) {
        template.convertAndSend(ttlTopic.getTopic(), message);
    }

    public void publishUserNotification(String message) {
        template.convertAndSend(userNotificationsTopic.getTopic(), message);
    }
}

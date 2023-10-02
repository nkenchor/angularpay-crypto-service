package io.angularpay.crypto.adapters.outbound;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

import static io.angularpay.crypto.common.Constants.*;

@Configuration
public class RedisOutboundConfiguration {

    @Bean
    ChannelTopic updatesTopic() {
        return new ChannelTopic(UPDATES_TOPIC);
    }

    @Bean
    ChannelTopic ttlTopic() {
        return new ChannelTopic(TTL_TOPIC);
    }

    @Bean
    ChannelTopic userNotificationsTopic() {
        return new ChannelTopic(USER_NOTIFICATIONS_TOPIC);
    }

}

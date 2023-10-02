package io.angularpay.crypto.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("angularpay")
@Data
public class AngularPayConfiguration {

    private String selfUrl;
    private String cipherUrl;
    private String schedulerUrl;
    private int pageSize;
    private int codecSizeInMB;
    private int maxUpdateRetry;
    private Redis redis;

    @Data
    public static class Redis {
        private String host;
        private int port;
        private int timeout;
    }
}

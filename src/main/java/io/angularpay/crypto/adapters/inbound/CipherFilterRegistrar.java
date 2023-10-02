package io.angularpay.crypto.adapters.inbound;

import io.angularpay.crypto.adapters.outbound.CipherServiceAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CipherFilterRegistrar {

    @ConditionalOnProperty(
            value = "angularpay.cipher.enabled",
            havingValue = "true",
            matchIfMissing = true)
    @Bean
    public FilterRegistrationBean<CipherFilter> registerPostCommentsRateLimiter(CipherServiceAdapter cipherServiceAdapter) {
        FilterRegistrationBean<CipherFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CipherFilter(cipherServiceAdapter));
        registrationBean.addUrlPatterns(
                "/crypto/requests",
                "/crypto/requests/*/amount",
                "/crypto/requests/*/exchange-rate",
                "/crypto/requests/*/investors",
                "/crypto/requests/*/bargains",
                "/crypto/requests/*/investors/*/amount",
                "/crypto/requests/*/investors/*/payment"
        );
        return registrationBean;
    }
}

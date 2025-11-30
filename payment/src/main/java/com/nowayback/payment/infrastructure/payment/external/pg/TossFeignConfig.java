package com.nowayback.payment.infrastructure.payment.external.pg;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TossFeignConfig {

    @Value("${payment.toss.secret-key}")
    private String secretKey;

    @Bean
    public RequestInterceptor tossAuthInterceptor() {
        return requestTemplate -> {
            String encodedAuth = Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

            requestTemplate.header("Authorization", "Basic " + encodedAuth);
        };
    }
}

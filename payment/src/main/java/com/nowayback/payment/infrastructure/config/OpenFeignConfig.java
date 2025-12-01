package com.nowayback.payment.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "com.nowayback.payment.infrastructure")
@Configuration
public class OpenFeignConfig {
}

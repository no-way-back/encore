package com.nowayback.reward.infrastructure.config;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class FeignConfig {

    /**
     * Feign 로그 레벨
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 재시도 정책
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
                100L,
                TimeUnit.SECONDS.toMillis(1L),
                3
        );
    }

    /**
     * 에러 디코더
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
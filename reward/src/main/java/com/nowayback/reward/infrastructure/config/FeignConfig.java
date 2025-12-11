package com.nowayback.reward.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfig {

    /**
     * 에러 디코더
     * HTTP 에러를 비즈니스 예외로 변환
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
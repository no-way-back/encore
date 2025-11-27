package com.nowayback.funding.infrastucture.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfig {

	/**
	 * Feign 로그 레벨
	 * FULL: 요청/응답 헤더, 바디, 메타데이터 모두 로깅
	 */
	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	/**
	 * 재시도 정책
	 * - 초기 간격: 100ms
	 * - 최대 간격: 1초 (지수 백오프)
	 * - 최대 시도: 3회
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
	 * HTTP 에러를 비즈니스 예외로 변환
	 */
	@Bean
	public ErrorDecoder errorDecoder() {
		return new FeignErrorDecoder();
	}
}
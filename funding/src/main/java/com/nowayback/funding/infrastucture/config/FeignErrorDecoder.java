package com.nowayback.funding.infrastucture.config;

import org.springframework.http.HttpStatus;

import com.nowayback.funding.infrastucture.exception.InfrastructureErrorCode;
import com.nowayback.funding.infrastucture.exception.InfrastructureException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

	private final ErrorDecoder defaultErrorDecoder = new Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		HttpStatus httpStatus = HttpStatus.resolve(response.status());
		String requestUrl = response.request().url();

		log.error("Feign 에러 발생 - method: {}, url: {}, status: {}",
			methodKey, requestUrl, response.status());

		// 서비스별 처리
		if (requestUrl.contains("/rewards")) {
			return handleRewardServiceError(httpStatus);
		} else if (requestUrl.contains("/payments")) {
			return handlePaymentServiceError(httpStatus);
		}

		// 기본 에러
		return handleCommonError(httpStatus);
	}

	/**
	 * Reward Service 에러 처리 (HTTP 상태 코드만 사용)
	 */
	private Exception handleRewardServiceError(HttpStatus httpStatus) {
		if (httpStatus == null) {
			return new InfrastructureException(InfrastructureErrorCode.REWARD_SERVICE_UNAVAILABLE);
		}

		return switch (httpStatus) {
			case BAD_REQUEST ->
				new InfrastructureException(InfrastructureErrorCode.REWARD_BAD_REQUEST);
			case CONFLICT ->
				new InfrastructureException(InfrastructureErrorCode.REWARD_CONFLICT);
			case SERVICE_UNAVAILABLE, BAD_GATEWAY ->
				new InfrastructureException(InfrastructureErrorCode.REWARD_SERVICE_UNAVAILABLE);
			default ->
				new InfrastructureException(InfrastructureErrorCode.EXTERNAL_SERVICE_ERROR);
		};
	}

	/**
	 * Payment Service 에러 처리 (HTTP 상태 코드만 사용)
	 */
	private Exception handlePaymentServiceError(HttpStatus httpStatus) {
		if (httpStatus == null) {
			return new InfrastructureException(InfrastructureErrorCode.PAYMENT_SERVICE_UNAVAILABLE);
		}

		return switch (httpStatus) {
			case BAD_REQUEST ->
				new InfrastructureException(InfrastructureErrorCode.PAYMENT_BAD_REQUEST);
			case CONFLICT, UNPROCESSABLE_ENTITY ->
				new InfrastructureException(InfrastructureErrorCode.PAYMENT_FAILED);
			case SERVICE_UNAVAILABLE, BAD_GATEWAY ->
				new InfrastructureException(InfrastructureErrorCode.PAYMENT_SERVICE_UNAVAILABLE);
			default ->
				new InfrastructureException(InfrastructureErrorCode.EXTERNAL_SERVICE_ERROR);
		};
	}

	/**
	 * 공통 에러 처리
	 */
	private Exception handleCommonError(HttpStatus httpStatus) {
		if (httpStatus == null) {
			return new InfrastructureException(InfrastructureErrorCode.EXTERNAL_SERVICE_ERROR);
		}

		return switch (httpStatus) {
			case GATEWAY_TIMEOUT, REQUEST_TIMEOUT ->
				new InfrastructureException(InfrastructureErrorCode.EXTERNAL_SERVICE_TIMEOUT);
			case SERVICE_UNAVAILABLE, BAD_GATEWAY ->
				new InfrastructureException(InfrastructureErrorCode.EXTERNAL_SERVICE_ERROR);
			default -> defaultErrorDecoder.decode("default", null);
		};
	}
}
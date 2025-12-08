package com.nowayback.funding.infrastructure.config;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import org.springframework.http.HttpStatus;

import com.nowayback.funding.domain.exception.FundingException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign 에러 디코더
 * 외부 서비스 호출 실패 시 적절한 예외로 변환
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

	private final ErrorDecoder defaultErrorDecoder = new Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		HttpStatus httpStatus = HttpStatus.resolve(response.status());
		String requestUrl = response.request().url();

		log.error("Feign 에러 발생 - method: {}, url: {}, status: {}, reason: {}",
			methodKey, requestUrl, response.status(), response.reason());

		// 서비스별 처리 (URL 기반 - 정확한 경로 체크)
		if (requestUrl.contains("/internal/rewards") || requestUrl.contains("/rewards")) {
			return handleRewardServiceError(httpStatus);
		} else if (requestUrl.contains("/internal/payments") || requestUrl.contains("/payments")) {
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
			return new FundingException(REWARD_SERVICE_UNAVAILABLE);
		}

		return switch (httpStatus) {
			case BAD_REQUEST ->
				new FundingException(REWARD_BAD_REQUEST);
			case NOT_FOUND ->
				new FundingException(REWARD_NOT_FOUND);
			case CONFLICT ->
				new FundingException(REWARD_CONFLICT);
			case SERVICE_UNAVAILABLE, BAD_GATEWAY ->
				new FundingException(REWARD_SERVICE_UNAVAILABLE);
			default ->
				new FundingException(EXTERNAL_SERVICE_ERROR);
		};
	}

	/**
	 * Payment Service 에러 처리 (HTTP 상태 코드만 사용)
	 */
	private Exception handlePaymentServiceError(HttpStatus httpStatus) {
		if (httpStatus == null) {
			return new FundingException(PAYMENT_SERVICE_UNAVAILABLE);
		}

		return switch (httpStatus) {
			case BAD_REQUEST ->
				new FundingException(PAYMENT_BAD_REQUEST);
			case NOT_FOUND ->
				new FundingException(PAYMENT_NOT_FOUND);
			case CONFLICT, UNPROCESSABLE_ENTITY ->
				new FundingException(PAYMENT_FAILED);
			case SERVICE_UNAVAILABLE, BAD_GATEWAY ->
				new FundingException(PAYMENT_SERVICE_UNAVAILABLE);
			default ->
				new FundingException(EXTERNAL_SERVICE_ERROR);
		};
	}

	/**
	 * 공통 에러 처리
	 */
	private Exception handleCommonError(HttpStatus httpStatus) {
		if (httpStatus == null) {
			return new FundingException(EXTERNAL_SERVICE_ERROR);
		}

		return switch (httpStatus) {
			case GATEWAY_TIMEOUT, REQUEST_TIMEOUT ->
				new FundingException(EXTERNAL_SERVICE_TIMEOUT);
			case SERVICE_UNAVAILABLE, BAD_GATEWAY ->
				new FundingException(EXTERNAL_SERVICE_ERROR);
			default -> defaultErrorDecoder.decode("default", null);
		};
	}
}
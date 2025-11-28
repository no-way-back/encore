package com.nowayback.funding.infrastucture.exception;

import org.springframework.http.HttpStatus;

import com.nowayback.funding.domain.exception.ErrorCode;

public enum InfrastructureErrorCode implements ErrorCode {

	// Reward Service
	REWARD_SERVICE_UNAVAILABLE("FD-INF-001", "티켓 서비스에 연결할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
	REWARD_BAD_REQUEST("FD-INF-002", "티켓 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	REWARD_CONFLICT("FD-INF-003", "티켓 재고가 부족합니다.", HttpStatus.CONFLICT),

	// Payment Service
	PAYMENT_SERVICE_UNAVAILABLE("FD-INF-101", "결제 서비스에 연결할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
	PAYMENT_BAD_REQUEST("FD-INF-102", "결제 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	PAYMENT_FAILED("FD-INF-103", "결제 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

	// 공통
	EXTERNAL_SERVICE_TIMEOUT("FD-INF-900", "외부 서비스 응답 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT),
	EXTERNAL_SERVICE_ERROR("FD-INF-901", "외부 서비스에서 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY);

	private final String code;
	private final String message;
	private final HttpStatus status;

	InfrastructureErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}
}

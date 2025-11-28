package com.nowayback.funding.application.exception;

import org.springframework.http.HttpStatus;

import com.nowayback.funding.presentation.exception.ErrorCode;

public enum FundingApplicationErrorCode implements ErrorCode {

	;

	private final String code;
	private final String message;
	private final HttpStatus status;

	FundingApplicationErrorCode(String code, String message, HttpStatus status) {
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

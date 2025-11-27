package com.nowayback.funding.presentation.exception;

import lombok.Getter;

@Getter
public class FundingException extends RuntimeException {

	private final ErrorCode errorCode;

	public FundingException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

}

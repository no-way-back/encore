package com.nowayback.funding.infrastucture.exception;

import com.nowayback.funding.presentation.exception.ErrorCode;
import com.nowayback.funding.presentation.exception.FundingException;

public class InfrastructureException extends FundingException {

	public InfrastructureException(ErrorCode errorCode) {
		super(errorCode);
	}
}

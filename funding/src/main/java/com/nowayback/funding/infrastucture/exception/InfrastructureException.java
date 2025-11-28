package com.nowayback.funding.infrastucture.exception;

import com.nowayback.funding.domain.exception.ErrorCode;
import com.nowayback.funding.domain.exception.FundingException;

public class InfrastructureException extends FundingException {

	public InfrastructureException(ErrorCode errorCode) {
		super(errorCode);
	}
}

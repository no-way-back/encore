package com.nowayback.funding.domain.exception;

import com.nowayback.funding.presentation.exception.ErrorCode;
import com.nowayback.funding.presentation.exception.FundingException;

public class FundingDomainException extends FundingException {

	public FundingDomainException(ErrorCode errorCode) {
		super(errorCode);
	}
}

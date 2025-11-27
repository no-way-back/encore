package com.nowayback.funding.application.exception;

import com.nowayback.funding.presentation.exception.ErrorCode;
import com.nowayback.funding.presentation.exception.FundingException;

public class FundingApplicationException extends FundingException {

	public FundingApplicationException(ErrorCode errorCode) {
		super(errorCode);
	}
}

package com.nowayback.funding.domain.fundingProjectStatistics.exception;

import com.nowayback.funding.presentation.exception.ErrorCode;
import com.nowayback.funding.presentation.exception.FundingException;

public class FundingProjectStatisticsDomainException extends FundingException {

	public FundingProjectStatisticsDomainException(ErrorCode errorCode) {
		super(errorCode);
	}
}

package com.nowayback.funding.application.fundingProjectStatistics.exception;

import com.nowayback.funding.presentation.exception.ErrorCode;
import com.nowayback.funding.presentation.exception.FundingException;

public class FundingProjectStatisticsApplicationException extends FundingException {

	public FundingProjectStatisticsApplicationException(ErrorCode errorCode) {
		super(errorCode);
	}
}

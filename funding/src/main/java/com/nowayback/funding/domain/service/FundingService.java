package com.nowayback.funding.domain.service;

import com.nowayback.funding.application.funding.dto.command.CancelFundingCommand;
import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.result.CancelFundingResult;
import com.nowayback.funding.application.funding.dto.result.CreateFundingResult;

public interface FundingService {

	CreateFundingResult createFunding(CreateFundingCommand command);

	CancelFundingResult cancelFunding(CancelFundingCommand command);
}

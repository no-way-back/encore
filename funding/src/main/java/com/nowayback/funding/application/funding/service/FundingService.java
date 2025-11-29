package com.nowayback.funding.application.funding.service;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.result.FundingResult;

public interface FundingService {

	FundingResult createFunding(CreateFundingCommand command);
}

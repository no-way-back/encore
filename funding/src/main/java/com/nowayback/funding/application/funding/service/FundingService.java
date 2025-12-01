package com.nowayback.funding.application.funding.service;

import com.nowayback.funding.application.funding.dto.command.CancelFundingCommand;
import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.command.GetMyFundingsCommand;
import com.nowayback.funding.application.funding.dto.result.CancelFundingResult;
import com.nowayback.funding.application.funding.dto.result.CreateFundingResult;
import com.nowayback.funding.application.funding.dto.result.GetMyFundingsResult;

public interface FundingService {

	CreateFundingResult createFunding(CreateFundingCommand command);

	CancelFundingResult cancelFunding(CancelFundingCommand command);

	GetMyFundingsResult getMyFundings(GetMyFundingsCommand command);
}

package com.nowayback.funding.application.fundingProjectStatistics.service;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;

public interface FundingProjectStatisticsService {

	void updateFundingStatusRate(CreateFundingCommand command);
}

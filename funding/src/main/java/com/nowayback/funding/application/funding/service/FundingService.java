package com.nowayback.funding.application.funding.service;

import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.CancelFundingCommand;
import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.command.GetMyFundingsCommand;
import com.nowayback.funding.application.funding.dto.command.GetProjectSponsorsCommand;
import com.nowayback.funding.application.funding.dto.result.CancelFundingResult;
import com.nowayback.funding.application.funding.dto.result.CreateFundingResult;
import com.nowayback.funding.application.funding.dto.result.FundingDetailResult;
import com.nowayback.funding.application.funding.dto.result.GetMyFundingsResult;
import com.nowayback.funding.application.funding.dto.result.GetProjectSponsorsResult;
import com.nowayback.funding.domain.funding.entity.Funding;

public interface FundingService {

	CreateFundingResult createFunding(CreateFundingCommand command);

	CancelFundingResult cancelFunding(CancelFundingCommand command);

	GetMyFundingsResult getMyFundings(GetMyFundingsCommand command);

	GetProjectSponsorsResult getProjectSponsors(GetProjectSponsorsCommand command);

	FundingDetailResult getFundingDetail(UUID fundingId);

	Funding completeFunding(UUID fundingId, UUID paymentId);

	Funding failFunding(UUID fundingId);
}

package com.nowayback.funding.presentation.Funding.dto.response;

import java.util.UUID;

import com.nowayback.funding.application.funding.dto.result.FundingResult;

public record CreateFundingResponse(
	UUID fundingId,
	String status,
	String message
) {
	public static CreateFundingResponse from(FundingResult result) {
		return new CreateFundingResponse(
			result.fundingId(),
			result.status(),
			result.message()
		);
	}
}

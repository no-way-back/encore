package com.nowayback.funding.presentation.funding.dto.response;

import com.nowayback.funding.application.funding.dto.result.CancelFundingResult;

import java.util.UUID;

public record CancelFundingResponse(
	UUID fundingId,
	String status,
	String message
) {
	public static CancelFundingResponse from(CancelFundingResult result) {
		return new CancelFundingResponse(
			result.fundingId(),
			result.status(),
			result.message()
		);
	}
}
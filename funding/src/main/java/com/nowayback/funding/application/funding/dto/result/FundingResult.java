package com.nowayback.funding.application.funding.dto.result;

import java.util.UUID;

public record FundingResult(
	UUID fundingId,
	String status,
	String message
) {
	public static FundingResult success(UUID fundingId) {
		return new FundingResult(fundingId, "SUCCESS", "후원이 완료되었습니다.");
	}

	public static FundingResult failure(String message) {
		return new FundingResult(null, "FAILURE", message);
	}
}

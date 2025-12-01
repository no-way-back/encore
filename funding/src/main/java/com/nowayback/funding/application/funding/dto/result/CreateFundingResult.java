package com.nowayback.funding.application.funding.dto.result;

import java.util.UUID;

public record CreateFundingResult(
	UUID fundingId,
	String status,
	String message
) {
	public static CreateFundingResult success(UUID fundingId) {
		return new CreateFundingResult(fundingId, "SUCCESS", "후원이 완료되었습니다.");
	}

	public static CreateFundingResult failure(String message) {
		return new CreateFundingResult(null, "FAILURE", message);
	}
}

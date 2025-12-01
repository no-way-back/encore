package com.nowayback.funding.application.funding.dto.result;

import java.util.UUID;

public record CancelFundingResult(
	UUID fundingId,
	String status,
	String message
) {
	public static CancelFundingResult success(UUID fundingId) {
		return new CancelFundingResult(fundingId, "SUCCESS", "후원이 취소되었습니다.");
	}

	public static CancelFundingResult failure(String message) {
		return new CancelFundingResult(null, "FAILURE", message);
	}
}

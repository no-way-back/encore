package com.nowayback.funding.application.funding.dto.command;

import java.util.UUID;

import com.nowayback.funding.domain.funding.entity.FundingStatus;

public record GetMyFundingsCommand(
	UUID userId,
	FundingStatus status,
	FundingPeriod period,
	FundingSortBy sortBy,
	int page,
	int size
) {
	public enum FundingPeriod {
		ONE_MONTH,
		THREE_MONTHS,
		ONE_YEAR,
		ALL
	}

	public enum FundingSortBy {
		LATEST,       // 최신순
		AMOUNT_DESC,  // 금액 높은 순
		AMOUNT_ASC    // 금액 낮은 순
	}
}

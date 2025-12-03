package com.nowayback.funding.presentation.funding.dto.request;

import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.GetMyFundingsCommand;
import com.nowayback.funding.domain.funding.entity.FundingStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GetMyFundingsRequest(
	FundingStatus status,
	GetMyFundingsCommand.FundingPeriod period,
	GetMyFundingsCommand.FundingSortBy sortBy,
	@Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
	Integer page,
	@Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
	@Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
	Integer size
) {
	public GetMyFundingsCommand toCommand(UUID userId) {
		return new GetMyFundingsCommand(
			userId,
			status,
			period != null ? period : GetMyFundingsCommand.FundingPeriod.ALL,
			sortBy != null ? sortBy : GetMyFundingsCommand.FundingSortBy.LATEST,
			page != null ? page : 0,
			size != null ? size : 20
		);
	}
}
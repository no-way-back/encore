package com.nowayback.funding.presentation.funding.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.nowayback.funding.application.funding.dto.result.GetMyFundingsResult;

public record GetMyFundingsResponse(
	List<FundingHistory> fundings,
	PageInfo pageInfo
) {
	public static GetMyFundingsResponse from(GetMyFundingsResult result) {
		List<FundingHistory> items = result.fundings().stream()
			.map(FundingHistory::from)
			.toList();

		PageInfo pageInfo = new PageInfo(
			result.pageInfo().page(),
			result.pageInfo().size(),
			result.pageInfo().totalElements(),
			result.pageInfo().totalPages()
		);

		return new GetMyFundingsResponse(items, pageInfo);
	}

	public record FundingHistory(
		UUID fundingId,
		UUID projectId,
		Long amount,
		Boolean hasReservation,
		String status,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		public static FundingHistory from(GetMyFundingsResult.FundingHistory history) {
			return new FundingHistory(
				history.fundingId(),
				history.projectId(),
				history.amount(),
				history.hasReservation(),
				history.status(),
				history.createdAt(),
				history.updatedAt()
			);
		}
	}

	public record PageInfo(
		int page,
		int size,
		long totalElements,
		int totalPages
	) {
	}
}
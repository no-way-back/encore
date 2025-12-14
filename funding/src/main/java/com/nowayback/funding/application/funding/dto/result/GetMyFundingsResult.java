package com.nowayback.funding.application.funding.dto.result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.nowayback.funding.domain.funding.entity.Funding;

public record GetMyFundingsResult(
	List<FundingHistory> fundings,
	PageInfo pageInfo
) {
	public static GetMyFundingsResult of(List<Funding> fundings, long totalElements, int page, int size) {
		List<FundingHistory> items = fundings.stream()
			.map(FundingHistory::from)
			.toList();

		PageInfo pageInfo = new PageInfo(
			page,
			size,
			totalElements,
			(int) Math.ceil((double) totalElements / size)
		);

		return new GetMyFundingsResult(items, pageInfo);
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
		public static FundingHistory from(Funding funding) {
			return new FundingHistory(
				funding.getId(),
				funding.getProjectId(),
				funding.getAmount(),
				funding.hasReservation(),
				funding.getStatus().name(),
				funding.getCreatedAt(),
				funding.getUpdatedAt()
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

package com.nowayback.funding.presentation.Funding.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.nowayback.funding.application.funding.dto.result.GetProjectSponsorsResult;

public record GetProjectSponsorsResponse(
	UUID projectId,
	Integer totalSponsors,
	Long totalAmount,
	List<SponsorHistory> sponsors,
	PageInfo pageInfo
) {
	public static GetProjectSponsorsResponse from(GetProjectSponsorsResult result) {
		List<SponsorHistory> sponsorHistories = result.sponsors().stream()
			.map(SponsorHistory::from)
			.toList();

		PageInfo pageInfo = new PageInfo(
			result.pageInfo().page(),
			result.pageInfo().size(),
			result.pageInfo().totalElements(),
			result.pageInfo().totalPages()
		);

		return new GetProjectSponsorsResponse(
			result.projectId(),
			result.totalSponsors(),
			result.totalAmount(),
			sponsorHistories,
			pageInfo
		);
	}

	public record SponsorHistory(
		UUID fundingId,
		UUID userId,
		Long amount,
		Boolean hasReservation,
		String status,
		LocalDateTime createdAt
	) {
		public static SponsorHistory from(GetProjectSponsorsResult.SponsorHistory sponsorHistory) {
			return new SponsorHistory(
				sponsorHistory.fundingId(),
				sponsorHistory.userId(),
				sponsorHistory.amount(),
				sponsorHistory.hasReservation(),
				sponsorHistory.status(),
				sponsorHistory.createdAt()
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

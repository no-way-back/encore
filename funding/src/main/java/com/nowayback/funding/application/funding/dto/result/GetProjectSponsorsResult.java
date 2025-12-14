package com.nowayback.funding.application.funding.dto.result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.nowayback.funding.domain.funding.entity.Funding;

public record GetProjectSponsorsResult(
	UUID projectId,
	Integer totalSponsors,
	Long totalAmount,
	List<SponsorHistory> sponsors,
	PageInfo pageInfo
) {
	public static GetProjectSponsorsResult of(
		UUID projectId,
		List<Funding> fundings,
		long totalElements,
		long totalAmount,
		int page,
		int size
	) {
		List<SponsorHistory> sponsorHistories = fundings.stream()
			.map(SponsorHistory::from)
			.toList();

		PageInfo pageInfo = new PageInfo(
			page,
			size,
			totalElements,
			(int) Math.ceil((double) totalElements / size)
		);

		return new GetProjectSponsorsResult(
			projectId,
			(int) totalElements,
			totalAmount,
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
		public static SponsorHistory from(Funding funding) {
			return new SponsorHistory(
				funding.getId(),
				funding.getUserId(),
				funding.getAmount(),
				funding.hasReservation(),
				funding.getStatus().name(),
				funding.getCreatedAt()
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

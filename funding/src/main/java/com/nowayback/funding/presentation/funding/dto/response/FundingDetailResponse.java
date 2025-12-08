package com.nowayback.funding.presentation.funding.dto.response;

import java.util.List;
import java.util.UUID;

import com.nowayback.funding.application.funding.dto.result.FundingDetailResult;
import com.nowayback.funding.domain.funding.entity.FundingStatus;

public record FundingDetailResponse(
	UUID fundingId,
	UUID projectId,
	UUID userId,
	Long amount,
	FundingStatus status,
	UUID paymentId,
	List<ReservationDetail> reservations
) {
	public static FundingDetailResponse from(FundingDetailResult result) {
		List<ReservationDetail> reservations = result.reservations().stream()
			.map(ReservationDetail::from)
			.toList();

		return new FundingDetailResponse(
			result.fundingId(),
			result.projectId(),
			result.userId(),
			result.amount(),
			result.status(),
			result.paymentId(),
			reservations
		);
	}

	public record ReservationDetail(
		UUID reservationId,
		UUID rewardId,
		UUID optionId,
		Integer quantity,
		Long amount
	) {
		public static ReservationDetail from(FundingDetailResult.ReservationInfo info) {
			return new ReservationDetail(
				info.reservationId(),
				info.rewardId(),
				info.optionId(),
				info.quantity(),
				info.amount()
			);
		}
	}
}
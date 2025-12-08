package com.nowayback.funding.application.funding.dto.result;

import java.util.List;
import java.util.UUID;

import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingReservation;
import com.nowayback.funding.domain.funding.entity.FundingStatus;

public record FundingDetailResult(
	UUID fundingId,
	UUID projectId,
	UUID userId,
	Long amount,
	FundingStatus status,
	UUID paymentId,
	List<ReservationInfo> reservations
) {
	public static FundingDetailResult from(Funding funding) {
		List<ReservationInfo> reservations = funding.getReservations().stream()
			.map(ReservationInfo::from)
			.toList();

		return new FundingDetailResult(
			funding.getId(),
			funding.getProjectId(),
			funding.getUserId(),
			funding.getAmount(),
			funding.getStatus(),
			funding.getPaymentId(),
			reservations
		);
	}

	public record ReservationInfo(
		UUID reservationId,
		UUID rewardId,
		UUID optionId,
		Integer quantity,
		Long amount
	) {
		public static ReservationInfo from(FundingReservation reservation) {
			return new ReservationInfo(
				reservation.getReservationId(),
				reservation.getRewardId(),
				reservation.getOptionId(),
				reservation.getQuantity(),
				reservation.getAmount()
			);
		}
	}
}

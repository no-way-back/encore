package com.nowayback.funding.domain.funding.entity;

import com.nowayback.funding.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_funding_reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundingReservation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "funding_id", nullable = false)
	private Funding funding;

	@Column(name = "reservation_id", nullable = false, unique = true)
	private UUID reservationId;

	@Column(name = "reward_id", nullable = false)
	private UUID rewardId;

	@Column(name = "option_id")
	private UUID optionId;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private Long amount;

	public static FundingReservation create(
		Funding funding,
		UUID reservationId,
		UUID rewardId,
		UUID optionId,
		Integer quantity,
		Long amount
	) {
		FundingReservation reservation = new FundingReservation();
		reservation.funding = funding;
		reservation.reservationId = reservationId;
		reservation.rewardId = rewardId;
		reservation.optionId = optionId;
		reservation.quantity = quantity;
		reservation.amount = amount;
		return reservation;
	}
}

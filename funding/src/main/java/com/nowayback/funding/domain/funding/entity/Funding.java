package com.nowayback.funding.domain.funding.entity;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nowayback.funding.domain.shared.BaseEntity;
import com.nowayback.funding.domain.exception.FundingException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_fundings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Funding extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "project_id", nullable = false)
	private UUID projectId;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@OneToMany(mappedBy = "funding", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FundingReservation> reservations = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private FundingStatus status;

	@Column(name = "payment_id")
	private UUID paymentId;

	@Column(name = "fail_reason")
	private String failReason;

	@Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
	private String idempotencyKey;

	public static Funding createFunding(
		UUID projectId,
		UUID userId,
		String idempotencyKey,
		Long amount
	) {
		Funding funding = new Funding();
		funding.projectId = projectId;
		funding.userId = userId;
		funding.status = FundingStatus.PENDING;
		funding.idempotencyKey = idempotencyKey;
		funding.amount = amount;
		return funding;
	}

	// ==================== 상태 관리 ====================

	public void completeFunding(UUID paymentId) {
		validateStatusTransition(FundingStatus.COMPLETED);
		this.paymentId = paymentId;
		this.status = FundingStatus.COMPLETED;
	}

	public void failFunding(String reason) {
		validateStatusTransition(FundingStatus.FAILED);
		this.status = FundingStatus.FAILED;
		this.failReason = reason;
	}

	public void cancelFunding() {
		if (this.status != FundingStatus.COMPLETED) {
			throw new FundingException(CANNOT_CANCEL_NON_COMPLETED);
		}
		this.status = FundingStatus.CANCELLED;
	}

	private void validateStatusTransition(FundingStatus newStatus) {
		if (this.status != FundingStatus.PENDING) {
			throw new FundingException(INVALID_STATUS_TRANSITION);
		}
	}

	// ==================== 금액 관리 ====================

	public void updateAmount(Long newAmount) {
		if (newAmount == null || newAmount <= 0) {
			throw new FundingException(INVALID_AMOUNT);
		}

		if (this.status != FundingStatus.PENDING) {
			throw new FundingException(INVALID_STATUS_TRANSITION);
		}

		this.amount = newAmount;
	}

	// ==================== 예약 관리 ====================

	public void addReservation(
		UUID reservationId,
		UUID rewardId,
		UUID optionId,
		Integer quantity,
		Long amount
	) {
		FundingReservation reservation = FundingReservation.create(
			this,
			reservationId,
			rewardId,
			optionId,
			quantity,
			amount
		);
		this.reservations.add(reservation);
	}

	public boolean hasReservation() {
		return this.reservations != null && !this.reservations.isEmpty();
	}

	public List<UUID> getReservationIds() {
		return this.reservations.stream()
			.map(FundingReservation::getReservationId)
			.toList();
	}
}

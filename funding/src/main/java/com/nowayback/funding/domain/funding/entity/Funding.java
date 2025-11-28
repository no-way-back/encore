package com.nowayback.funding.domain.funding.entity;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.util.UUID;

import com.nowayback.funding.domain.shared.BaseEntity;
import com.nowayback.funding.domain.exception.FundingException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	@Column(name = "reservation_id")
	private UUID reservationId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private FundingStatus status;

	@Column(name = "payment_id")
	private Long paymentId;

	@Column(name = "fail_reason")
	private String failReason;

	@Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
	private String idempotencyKey;

	public static Funding createFunding(
		UUID projectId,
		UUID userId,
		Long amount,
		String idempotencyKey
	) {
		Funding funding = new Funding();
		funding.projectId = projectId;
		funding.userId = userId;
		funding.amount = amount;
		funding.status = FundingStatus.PENDING;
		funding.idempotencyKey = idempotencyKey;
		return funding;
	}

	public void completeFunding(UUID reservationId, UUID paymentId) {
		validateStatusTransition(FundingStatus.COMPLETED);
		this.reservationId = reservationId;
		this.paymentId = paymentId;
		this.status = FundingStatus.COMPLETED;
	}

	public void failFunding(String reason) {
		validateStatusTransition(FundingStatus.FAILED);
		this.failReason = reason;
		this.status = FundingStatus.FAILED;
	}

	public void cancelFunding() {
		if (this.status != FundingStatus.COMPLETED) {
			throw new FundingException(CANNOT_CANCEL_NON_COMPLETED);
		}
		this.status = FundingStatus.CANCELLED;
	}

	public void refundFunding() {
		if (this.status != FundingStatus.COMPLETED) {
			throw new FundingException(CANNOT_CANCEL_NON_COMPLETED);
		}
		this.status = FundingStatus.REFUNDED;
	}

	public boolean hasReservation() {
		return this.reservationId != null;
	}

	private void validateStatusTransition(FundingStatus fundingNewStatus) {
		if (this.status != FundingStatus.PENDING) {
			throw new FundingException(INVALID_STATUS_TRANSITION);
		}
	}
}

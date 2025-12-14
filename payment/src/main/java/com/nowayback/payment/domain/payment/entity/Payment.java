package com.nowayback.payment.domain.payment.entity;

import com.nowayback.payment.domain.shared.BaseEntity;
import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import com.nowayback.payment.domain.payment.vo.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id", updatable = false, nullable = false))
    private UserId userId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "funding_id", updatable = false, nullable = false))
    private FundingId fundingId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "project_id", updatable = false, nullable = false))
    private ProjectId projectId;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", updatable = false, nullable = false))
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "pgMethod", column = @Column(name = "pg_method", length = 20)),
            @AttributeOverride(name = "pgPaymentKey", column = @Column(name = "pg_payment_key", length = 100)),
            @AttributeOverride(name = "pgOrderId", column = @Column(name = "pg_order_id"))
    })
    private PgInfo pgInfo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "refundAccountBank", column = @Column(name = "refund_account_bank", length = 20)),
            @AttributeOverride(name = "refundAccountNumber", column = @Column(name = "refund_account_number", length = 50)),
            @AttributeOverride(name = "refundAccountHolderName", column = @Column(name = "refund_account_holder_name", length = 20))
    })
    private RefundAccountInfo refundAccountInfo;

    @Column(name = "refund_reason", length = 255)
    private String refundReason;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    private Payment(UserId userId, FundingId fundingId, ProjectId projectId, Money amount, PgInfo pgInfo, RefundAccountInfo refundAccountInfo) {
        this.userId = userId;
        this.fundingId = fundingId;
        this.projectId = projectId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.pgInfo = pgInfo;
        this.refundAccountInfo = refundAccountInfo;
    }

    /* Factory Method */

    public static Payment create(UserId userId, FundingId fundingId, ProjectId projectId, Money amount) {
        validatePayment(userId, fundingId, projectId, amount);
        return new Payment(userId, fundingId, projectId, amount, null, null);
    }

    /* Business Methods */

    // Deprecated: Use confirm(PgInfo pgInfo) instead
    public void complete(LocalDateTime approvedAt) {
        changeStatus(PaymentStatus.COMPLETED);
        this.approvedAt = approvedAt;
    }

    public void confirm(PgInfo pgInfo, LocalDateTime approvedAt) {
        validatePgInfo(pgInfo);
        changeStatus(PaymentStatus.COMPLETED);
        this.pgInfo = pgInfo;
        this.approvedAt = approvedAt;
    }

    public void fail(LocalDateTime failedAt) {
        changeStatus(PaymentStatus.FAILED);
        this.failedAt = failedAt;
    }

    public void refund(RefundAccountInfo refundAccountInfo, String reason, LocalDateTime refundedAt) {
        changeStatus(PaymentStatus.REFUNDED);
        this.refundAccountInfo = refundAccountInfo;
        this.refundReason = reason;
        this.refundedAt = refundedAt;
    }

    private void changeStatus(PaymentStatus newStatus) {
        validateStatus(newStatus);
        if (!this.status.canTransitionTo(newStatus)) {
            throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_STATUS_TRANSITION);
        }
        this.status = newStatus;
    }

    /* Validation Methods */

    private static void validatePayment(UserId userId, FundingId fundingId, ProjectId projectId, Money amount) {
        validateUserId(userId);
        validateFundingId(fundingId);
        validateProjectId(projectId);
        validateAmount(amount);
    }

    private static void validateUserId(UserId userId) {
        if (userId == null) {
            throw new PaymentException(PaymentErrorCode.NULL_USER_ID_OBJECT);
        }
    }

    private static void validateFundingId(FundingId fundingId) {
        if (fundingId == null) {
            throw new PaymentException(PaymentErrorCode.NULL_FUNDING_ID_OBJECT);
        }
    }

    private static void validateProjectId(ProjectId projectId) {
        if (projectId == null) {
            throw new PaymentException(PaymentErrorCode.NULL_PAYMENT_PROJECT_ID_OBJECT);
        }
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw new PaymentException(PaymentErrorCode.NULL_PAYMENT_MONEY_OBJECT);
        }
    }

    private static void validateStatus(PaymentStatus status) {
        if (status == null) {
            throw new PaymentException(PaymentErrorCode.NULL_PAYMENT_STATUS_OBJECT);
        }
    }

    private static void validatePgInfo(PgInfo pgInfo) {
        if (pgInfo == null) {
            throw new PaymentException(PaymentErrorCode.NULL_PG_INFO_OBJECT);
        }
    }
}

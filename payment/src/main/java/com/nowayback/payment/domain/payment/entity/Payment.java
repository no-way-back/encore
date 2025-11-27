package com.nowayback.payment.domain.payment.entity;

import com.nowayback.payment.domain.shared.BaseEntity;
import com.nowayback.payment.domain.exception.PaymentDomainErrorCode;
import com.nowayback.payment.domain.exception.PaymentDomainException;
import com.nowayback.payment.domain.payment.vo.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @AttributeOverride(name = "id", column = @Column(name = "payment_id", updatable = false, nullable = false))
    private FundingId fundingId;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", updatable = false, nullable = false))
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "pgMethod", column = @Column(name = "pg_method", updatable = false, nullable = false, length = 20)),
            @AttributeOverride(name = "pgPaymentKey", column = @Column(name = "pg_payment_key", updatable = false, nullable = false, length = 100)),
            @AttributeOverride(name = "pgTransactionId", column = @Column(name = "pg_transaction_id", updatable = false, nullable = false, length = 100)),
            @AttributeOverride(name = "pgOrderId", column = @Column(name = "pg_order_id", updatable = false, nullable = false, length = 255))
    })
    private PgInfo pgInfo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "refundAccountBank", column = @Column(name = "refund_account_bank", length = 20)),
            @AttributeOverride(name = "refundAccountNumber", column = @Column(name = "refund_account_number", length = 50)),
            @AttributeOverride(name = "refundAccountHolderName", column = @Column(name = "refund_account_holder_name", length = 20))
    })
    private RefundAccountInfo refundAccountInfo;

    private Payment(UserId userId, FundingId fundingId, Money amount, PaymentStatus status, PgInfo pgInfo, RefundAccountInfo refundAccountInfo) {
        this.userId = userId;
        this.fundingId = fundingId;
        this.amount = amount;
        this.status = status;
        this.pgInfo = pgInfo;
        this.refundAccountInfo = refundAccountInfo;
    }

    /* Factory Method */

    public static Payment create(UserId userId, FundingId fundingId, Money amount, PaymentStatus status, PgInfo pgInfo) {
        validatePayment(userId, fundingId, amount, status, pgInfo);
        return new Payment(userId, fundingId, amount, status, pgInfo, null);
    }

    /* Business Methods */

    public void changeStatus(PaymentStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new PaymentDomainException(PaymentDomainErrorCode.INVALID_PAYMENT_STATUS_TRANSITION);
        }
        this.status = newStatus;
    }

    public void setRefundAccountInfo(RefundAccountInfo refundAccountInfo) {
        validateRefundAccountInfo(refundAccountInfo);
        this.refundAccountInfo = refundAccountInfo;
    }

    /* Validation Methods */

    private static void validatePayment(UserId userId, FundingId fundingId, Money amount, PaymentStatus status, PgInfo pgInfo) {
        validateUserId(userId);
        validateFundingId(fundingId);
        validateAmount(amount);
        validateStatus(status);
        validatePgInfo(pgInfo);
    }

    private static void validateUserId(UserId userId) {
        if (userId == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_USER_ID_OBJECT);
        }
    }

    private static void validateFundingId(FundingId fundingId) {
        if (fundingId == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_FUNDING_ID_OBJECT);
        }
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_PAYMENT_MONEY_OBJECT);
        }
    }

    private static void validateStatus(PaymentStatus status) {
        if (status == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_PAYMENT_STATUS_OBJECT);
        }
    }

    private static void validatePgInfo(PgInfo pgInfo) {
        if (pgInfo == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_PG_INFO_OBJECT);
        }
    }

    private static void validateRefundAccountInfo(RefundAccountInfo refundAccountInfo) {
        if (refundAccountInfo == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_REFUND_ACCOUNT_INFO_OBJECT);
        }
    }
}

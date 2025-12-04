package com.nowayback.payment.domain.payment.entity;

import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import com.nowayback.payment.domain.payment.vo.Money;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import com.nowayback.payment.domain.shared.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_payment_status_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentStatusLog extends BaseCreateEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "payment_id", updatable = false, nullable = false)
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "prev_status", updatable = false, nullable = false)
    private PaymentStatus prevStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "curr_status", updatable = false, nullable = false)
    private PaymentStatus currStatus;

    @Column(name = "reason", length = 500)
    private String reason;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", updatable = false, nullable = false))
    private Money amount;

    private PaymentStatusLog(UUID paymentId, PaymentStatus prevStatus, PaymentStatus currStatus, String reason, Money amount) {
        this.paymentId = paymentId;
        this.prevStatus = prevStatus;
        this.currStatus = currStatus;
        this.reason = reason;
        this.amount = amount;
    }

    /* Factory Method */

    public static PaymentStatusLog create(UUID paymentId, PaymentStatus prevStatus, PaymentStatus currStatus, String reason, Money amount) {
        validatePaymentStatusLog(paymentId, prevStatus, currStatus, reason, amount);
        return new PaymentStatusLog(paymentId, prevStatus, currStatus, reason, amount);
    }

    /* Validation Methods */

    private static void validatePaymentStatusLog(UUID paymentId, PaymentStatus prevStatus, PaymentStatus currStatus, String reason, Money amount) {
        validatePaymentId(paymentId);
        validatePrevStatus(prevStatus);
        validateCurrStatus(currStatus);
        validateAmount(amount);
    }

    private static void validatePaymentId(UUID paymentId) {
        if (paymentId == null) {
            throw new PaymentException(PaymentErrorCode.NULL_PAYMENT_ID_OBJECT);
        }
    }

    private static void validatePrevStatus(PaymentStatus status) {
        if (status == null) {
            throw new PaymentException(PaymentErrorCode.NULL_PREV_PAYMENT_STATUS_OBJECT);
        }
    }

    private static void validateCurrStatus(PaymentStatus status) {
        if (status == null) {
            throw new PaymentException(PaymentErrorCode.NULL_CURR_PAYMENT_STATUS_OBJECT);
        }
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw new PaymentException(PaymentErrorCode.NULL_PAYMENT_AMOUNT_OBJECT);
        }
    }
}

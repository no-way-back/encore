package com.nowayback.payment.domain.settlement.entity;

import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import com.nowayback.payment.domain.settlement.vo.Money;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;
import com.nowayback.payment.domain.shared.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_settlement_status_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementStatusLog extends BaseCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "settlement_id", updatable = false, nullable = false)
    private UUID settlementId;

    @Enumerated(EnumType.STRING)
    @Column(name = "prev_status", updatable = false, nullable = false)
    private SettlementStatus prevStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "curr_status", updatable = false, nullable = false)
    private SettlementStatus currStatus;

    @Column(name = "reason", length = 500)
    private String reason;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", updatable = false, nullable = false))
    private Money amount;

    private SettlementStatusLog(UUID settlementId, SettlementStatus prevStatus, SettlementStatus currStatus, String reason, Money amount) {
        this.settlementId = settlementId;
        this.prevStatus = prevStatus;
        this.currStatus = currStatus;
        this.reason = reason;
        this.amount = amount;
    }

    /* Factory Method */

    public static SettlementStatusLog create(UUID settlementId, SettlementStatus prevStatus, SettlementStatus currStatus, String reason, Money amount) {
        validateSettlementStatusLog(settlementId, prevStatus, currStatus, reason, amount);
        return new SettlementStatusLog(settlementId, prevStatus, currStatus, reason, amount);
    }

    /* Validation Methods */

    private static void validateSettlementStatusLog(UUID settlementId, SettlementStatus prevStatus, SettlementStatus currStatus, String reason, Money amount) {
        validateSettlementId(settlementId);
        validatePrevStatus(prevStatus);
        validateCurrStatus(currStatus);
        validateAmount(amount);
    }

    private static void validateSettlementId(UUID settlementId) {
        if (settlementId == null) {
            throw new PaymentException(PaymentErrorCode.NULL_SETTLEMENT_ID_OBJECT);
        }
    }

    private static void validatePrevStatus(SettlementStatus status) {
        if (status == null) {
            throw new PaymentException(PaymentErrorCode.NULL_PREV_SETTLEMENT_STATUS_OBJECT);
        }
    }

    private static void validateCurrStatus(SettlementStatus status) {
        if (status == null) {
            throw new PaymentException(PaymentErrorCode.NULL_CURR_SETTLEMENT_STATUS_OBJECT);
        }
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw new PaymentException(PaymentErrorCode.NULL_SETTLEMENT_AMOUNT_OBJECT);
        }
    }
}

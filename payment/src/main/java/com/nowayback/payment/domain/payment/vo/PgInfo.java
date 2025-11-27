package com.nowayback.payment.domain.payment.vo;

import com.nowayback.payment.domain.exception.PaymentDomainErrorCode;
import com.nowayback.payment.domain.exception.PaymentDomainException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PgInfo {

    private String pgMethod;
    private String pgPaymentKey;
    private String pgTransactionId;
    private String pgOrderId;

    private PgInfo(String pgMethod, String pgPaymentKey, String pgTransactionId, String pgOrderId) {
        this.pgMethod = pgMethod;
        this.pgPaymentKey = pgPaymentKey;
        this.pgTransactionId = pgTransactionId;
        this.pgOrderId = pgOrderId;
    }

    public static PgInfo of(String pgMethod, String pgPaymentKey, String pgTransactionId, String pgOrderId) {
        validatePgInfo(pgMethod, pgPaymentKey, pgTransactionId, pgOrderId);
        return new PgInfo(pgMethod, pgPaymentKey, pgTransactionId, pgOrderId);
    }

    private static void validatePgInfo(String pgMethod, String pgPaymentKey, String pgTransactionId, String pgOrderId) {
        validatePgMethod(pgMethod);
        validatePgPaymentKey(pgPaymentKey);
        validatePgTransactionId(pgTransactionId);
        validatePgOrderId(pgOrderId);
    }

    private static void validatePgMethod(String pgMethod) {
        if (pgMethod == null || pgMethod.trim().isEmpty()) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_PG_METHOD_VALUE);
        }
    }

    private static void validatePgPaymentKey(String pgPaymentKey) {
        if (pgPaymentKey == null || pgPaymentKey.trim().isEmpty()) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_PG_PAYMENT_KEY_VALUE);
        }
    }

    private static void validatePgTransactionId(String pgTransactionId) {
        if (pgTransactionId == null || pgTransactionId.trim().isEmpty()) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_PG_TRANSACTION_ID_VALUE);
        }
    }

    private static void validatePgOrderId(String pgOrderId) {
        if (pgOrderId == null || pgOrderId.trim().isEmpty()) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_PG_ORDER_ID_VALUE);
        }
    }
}

package com.nowayback.payment.domain.payment.vo;

import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
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
    private String pgOrderId;

    private PgInfo(String pgMethod, String pgPaymentKey, String pgOrderId) {
        this.pgMethod = pgMethod;
        this.pgPaymentKey = pgPaymentKey;
        this.pgOrderId = pgOrderId;
    }

    public static PgInfo of(String pgMethod, String pgPaymentKey, String pgOrderId) {
        validatePgInfo(pgMethod, pgPaymentKey, pgOrderId);
        return new PgInfo(pgMethod, pgPaymentKey, pgOrderId);
    }

    private static void validatePgInfo(String pgMethod, String pgPaymentKey, String pgOrderId) {
        validatePgMethod(pgMethod);
        validatePgPaymentKey(pgPaymentKey);
        validatePgOrderId(pgOrderId);
    }

    private static void validatePgMethod(String pgMethod) {
        if (pgMethod == null || pgMethod.trim().isEmpty()) {
            throw new PaymentException(PaymentErrorCode.NULL_PG_METHOD_VALUE);
        }
    }

    private static void validatePgPaymentKey(String pgPaymentKey) {
        if (pgPaymentKey == null || pgPaymentKey.trim().isEmpty()) {
            throw new PaymentException(PaymentErrorCode.NULL_PG_PAYMENT_KEY_VALUE);
        }
    }

    private static void validatePgOrderId(String pgOrderId) {
        if (pgOrderId == null || pgOrderId.trim().isEmpty()) {
            throw new PaymentException(PaymentErrorCode.NULL_PG_ORDER_ID_VALUE);
        }
    }
}

package com.nowayback.payment.fixture;

import com.nowayback.payment.domain.payment.entity.PaymentStatusLog;
import com.nowayback.payment.domain.payment.vo.Money;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;

import java.util.UUID;

public class PaymentStatusLogFixture {
    public static final UUID PAYMENT_STATUS_LOG_ID = UUID.randomUUID();

    public static final UUID PAYMENT_ID = UUID.randomUUID();
    public static final PaymentStatus PREV_STATUS = PaymentStatus.PENDING;
    public static final PaymentStatus CURR_STATUS = PaymentStatus.COMPLETED;
    public static final String REASON = "Payment completed successfully";

    public static final long AMOUNT_VALUE = 10_000L;
    public static final Money AMOUNT = Money.of(AMOUNT_VALUE);

    /* Payment Status Log Entity */

    public static PaymentStatusLog createPaymentStatusLog() {
        return PaymentStatusLog.create(
                PAYMENT_ID,
                PREV_STATUS,
                CURR_STATUS,
                REASON,
                AMOUNT
        );
    }
}

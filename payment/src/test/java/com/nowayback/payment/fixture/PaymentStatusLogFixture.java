package com.nowayback.payment.fixture;

import com.nowayback.payment.domain.payment.entity.PaymentStatusLog;
import com.nowayback.payment.domain.payment.vo.Money;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.UUID;

public class PaymentStatusLogFixture {
    public static final UUID PAYMENT_STATUS_LOG_ID = UUID.randomUUID();

    public static final UUID PAYMENT_ID = UUID.randomUUID();
    public static final PaymentStatus PREV_STATUS = PaymentStatus.PENDING;
    public static final PaymentStatus CURR_STATUS = PaymentStatus.COMPLETED;
    public static final String REASON = "Payment completed successfully";

    public static final long AMOUNT_VALUE = 10_000L;
    public static final Money AMOUNT = Money.of(AMOUNT_VALUE);

    public static final int PAGE = 0;
    public static final int SIZE = 10;
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "createdAt");
    public static final Pageable PAGEABLE = PageRequest.of(PAGE, SIZE, SORT);

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

    public static final List<PaymentStatusLog> PAYMENT_STATUS_LOGS = List.of(
            createPaymentStatusLog(),
            createPaymentStatusLog()
    );

    /* Payment Status Log Result */

    public static final Page<PaymentStatusLog> PAYMENT_STATUS_LOGS_PAGE = new PageImpl<>(PAYMENT_STATUS_LOGS, PAGEABLE, PAYMENT_STATUS_LOGS.size());
}

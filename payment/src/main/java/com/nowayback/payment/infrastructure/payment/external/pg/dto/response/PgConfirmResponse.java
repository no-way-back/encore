package com.nowayback.payment.infrastructure.payment.external.pg.dto.response;

import java.time.OffsetDateTime;

public record PgConfirmResponse (
        String paymentKey,
        String orderId,
        Long totalAmount,
        OffsetDateTime approvedAt,
        String status
) {
}

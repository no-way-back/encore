package com.nowayback.payment.infrastructure.payment.external.pg.dto.request;

public record PgConfirmRequest (
        String paymentKey,
        String orderId,
        Long amount
) {
}

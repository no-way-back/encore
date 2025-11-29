package com.nowayback.payment.application.payment.service.pg.dto;

import java.time.LocalDateTime;

public record PgRefundResult (
        String paymentKey,
        String orderId,
        String cancelReason,
        LocalDateTime canceledAt,
        Long cancelAmount,
        String cancelStatus
) {
}

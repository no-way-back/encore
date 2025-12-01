package com.nowayback.payment.application.payment.service.pg.dto;

import java.time.LocalDateTime;

public record PgConfirmResult (
        String paymentKey,
        String orderId,
        Long totalAmount,
        LocalDateTime approvedAt,
        String status
) {
}

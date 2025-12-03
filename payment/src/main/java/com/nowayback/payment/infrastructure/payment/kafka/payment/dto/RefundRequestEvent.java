package com.nowayback.payment.infrastructure.payment.kafka.payment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundRequestEvent (
        UUID paymentId,
        UUID projectId,
        String reason,
        LocalDateTime occurredAt
) {
}

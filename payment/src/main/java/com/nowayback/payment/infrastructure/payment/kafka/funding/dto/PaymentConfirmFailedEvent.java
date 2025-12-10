package com.nowayback.payment.infrastructure.payment.kafka.funding.dto;

import java.util.UUID;

public record PaymentConfirmFailedEvent (
        UUID fundingId
) {
}

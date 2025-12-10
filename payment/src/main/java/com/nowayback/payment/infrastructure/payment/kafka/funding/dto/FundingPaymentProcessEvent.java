package com.nowayback.payment.infrastructure.payment.kafka.funding.dto;

import java.util.UUID;

public record FundingPaymentProcessEvent (
        UUID fundingId,
        UUID projectId,
        UUID userId,
        long amount
){
}

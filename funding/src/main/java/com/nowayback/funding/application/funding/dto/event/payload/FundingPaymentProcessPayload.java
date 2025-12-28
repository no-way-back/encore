package com.nowayback.funding.application.funding.dto.event.payload;

import java.util.UUID;

public record FundingPaymentProcessPayload(
        UUID fundingId,
        UUID projectId,
        UUID userId,
        Long amount
) {}
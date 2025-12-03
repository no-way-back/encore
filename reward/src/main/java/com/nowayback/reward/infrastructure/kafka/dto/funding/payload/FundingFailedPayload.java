package com.nowayback.reward.infrastructure.kafka.dto.funding.payload;

import java.util.UUID;

public record FundingFailedPayload(
        UUID fundingId,
        UUID projectId,
        UUID userId,
        UUID reservationId
) {
}
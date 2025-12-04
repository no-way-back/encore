package com.nowayback.reward.infrastructure.kafka.dto.funding.payload;

import java.util.UUID;

public record FundingRefundPayload(
        UUID fundingId,
        UUID reservationId
) {
}
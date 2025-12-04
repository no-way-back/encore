package com.nowayback.reward.infrastructure.kafka.dto.funding.event;

import com.nowayback.reward.infrastructure.kafka.constant.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.funding.payload.FundingRefundPayload;

import java.time.LocalDateTime;

public record FundingRefundEvent(
        String eventId,
        EventType eventType,
        LocalDateTime timestamp,
        FundingRefundPayload payload
) {
}
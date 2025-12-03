package com.nowayback.reward.infrastructure.kafka.dto.funding.event;

import com.nowayback.reward.infrastructure.kafka.constant.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.funding.payload.FundingFailedPayload;

import java.time.LocalDateTime;

public record FundingFailedEvent(
        String eventId,
        EventType eventType,
        LocalDateTime timestamp,
        FundingFailedPayload payload
) {
}
package com.nowayback.reward.infrastructure.kafka.dto.funding.event;

import com.nowayback.reward.domain.outbox.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.funding.payload.FundingFailedPayload;

import java.time.LocalDateTime;
import java.util.UUID;

public record FundingFailedEvent(
        UUID eventId,
        EventType eventType,
        LocalDateTime timestamp,
        FundingFailedPayload payload
) {
}
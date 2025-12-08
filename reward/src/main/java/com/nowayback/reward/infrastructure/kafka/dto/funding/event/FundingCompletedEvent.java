package com.nowayback.reward.infrastructure.kafka.dto.funding.event;

import com.nowayback.reward.infrastructure.kafka.constant.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.funding.payload.FundingCompletedPayload;

import java.time.LocalDateTime;
import java.util.UUID;

public record FundingCompletedEvent(
        UUID eventId,
        EventType eventType,
        LocalDateTime timestamp,
        FundingCompletedPayload payload
) {}
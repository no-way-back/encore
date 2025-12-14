package com.nowayback.reward.infrastructure.kafka.dto.funding.event;

import com.nowayback.reward.domain.outbox.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.funding.payload.FundingRefundPayload;

import java.time.LocalDateTime;
import java.util.UUID;

public record FundingRefundEvent(
        UUID eventId,
        EventType eventType,
        LocalDateTime timestamp,
        FundingRefundPayload payload
) {
}
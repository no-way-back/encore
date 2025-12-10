package com.nowayback.reward.infrastructure.kafka.dto.funding.event;

import com.nowayback.reward.infrastructure.kafka.constant.EventType;

import java.util.UUID;

public record ProjectFundingSuccessEvent(
        UUID eventId,
        EventType eventType,
        ProjectFundingSuccessPayload payload
) {
    public record ProjectFundingSuccessPayload(
            UUID fundingId,
            UUID projectId
    ) {}
}
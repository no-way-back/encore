package com.nowayback.funding.application.funding.dto.event;

import com.nowayback.funding.application.funding.dto.event.payload.FundingFailedPayload;
import com.nowayback.funding.domain.event.EventType;
import com.nowayback.funding.domain.event.OutboxEventMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public record FundingFailedEvent(
        UUID eventId,
        EventType eventType,
        LocalDateTime timestamp,
        FundingFailedPayload payload
) implements OutboxEventMetadata {

    @Override
    public String getAggregateType() {
        return "FUNDING";
    }

    @Override
    public UUID getAggregateId() {
        return payload.fundingId();
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    public static FundingFailedEvent from(UUID fundingId) {
        return new FundingFailedEvent(
                UUID.randomUUID(),
                EventType.FUNDING_FAILED,
                LocalDateTime.now(),
                new FundingFailedPayload(fundingId)
        );
    }
}
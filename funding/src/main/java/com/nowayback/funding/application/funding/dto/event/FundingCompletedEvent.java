package com.nowayback.funding.application.funding.dto.event;

import com.nowayback.funding.application.funding.dto.event.payload.FundingCompletedPayload;
import com.nowayback.funding.domain.event.EventType;
import com.nowayback.funding.domain.event.OutboxEventMetadata;
import com.nowayback.funding.domain.funding.entity.Funding;

import java.time.LocalDateTime;
import java.util.UUID;

public record FundingCompletedEvent(
        UUID eventId,
        EventType eventType,
        LocalDateTime timestamp,
        FundingCompletedPayload payload
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

    public static FundingCompletedEvent from(Funding funding) {
        return new FundingCompletedEvent(
                UUID.randomUUID(),
                EventType.FUNDING_COMPLETED,
                LocalDateTime.now(),
                FundingCompletedPayload.from(funding)
        );
    }
}
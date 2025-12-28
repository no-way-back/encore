package com.nowayback.funding.application.funding.dto.event;

import com.nowayback.funding.application.funding.dto.event.payload.FundingRefundPayload;
import com.nowayback.funding.domain.event.EventType;
import com.nowayback.funding.domain.event.OutboxEventMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public record FundingRefundEvent(
        UUID eventId,
        EventType eventType,
        LocalDateTime timestamp,
        FundingRefundPayload payload
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

    public static FundingRefundEvent from(UUID fundingId) {
        return new FundingRefundEvent(
                UUID.randomUUID(),
                EventType.FUNDING_REFUND,
                LocalDateTime.now(),
                new FundingRefundPayload(fundingId)
        );
    }
}
package com.nowayback.funding.application.funding.dto.event;

import com.nowayback.funding.application.funding.dto.event.payload.FundingPaymentProcessPayload;
import com.nowayback.funding.domain.event.EventType;
import com.nowayback.funding.domain.event.OutboxEventMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public record FundingPaymentProcessEvent(
        UUID eventId,
        EventType eventType,
        LocalDateTime timestamp,
        FundingPaymentProcessPayload payload
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

    public static FundingPaymentProcessEvent of(
            UUID fundingId,
            UUID projectId,
            UUID userId,
            Long amount
    ) {
        return new FundingPaymentProcessEvent(
                UUID.randomUUID(),
                EventType.FUNDING_PAYMENT_PROCESS,
                LocalDateTime.now(),
                new FundingPaymentProcessPayload(fundingId, projectId, userId, amount)
        );
    }
}
package com.nowayback.funding.domain.event;

import java.util.UUID;

public interface OutboxEventMetadata {
    String getAggregateType();
    UUID getAggregateId();
    EventType getEventType();
    Object getPayload();
}
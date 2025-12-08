package com.nowayback.project.domain.shard;

import com.nowayback.project.domain.outbox.vo.AggregateType;
import com.nowayback.project.domain.outbox.vo.EventType;
import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID getAggregateId();
    LocalDateTime getOccurredAt();
    AggregateType getAggregateType();
    EventType getEventType();
}

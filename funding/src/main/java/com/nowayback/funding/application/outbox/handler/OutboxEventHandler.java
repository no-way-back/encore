package com.nowayback.funding.application.outbox.handler;

import com.nowayback.funding.domain.event.OutboxEventMetadata;
import com.nowayback.funding.domain.outbox.entity.Outbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventHandler {

    public Outbox handle(OutboxEventMetadata event) {
        return Outbox.createOutbox(
                event.getAggregateType(),
                event.getAggregateId(),
                event.getEventType(),
                event.getPayload()
        );
    }
}
package com.nowayback.project.application.outbox.event;

import com.nowayback.project.application.outbox.event.payload.EventPayload;
import com.nowayback.project.domain.outbox.Outbox;
import com.nowayback.project.domain.outbox.vo.AggregateType;
import com.nowayback.project.domain.outbox.vo.EventType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(EventType type, EventPayload payload, AggregateType aggregateType, UUID aggregateId) {
        log.info(
            "[OutboxEventPublisher.publish] type={}, payload={}, AggregateType={}, aggregateId={}",
            type, payload, aggregateType, aggregateId);
        Outbox outbox = Outbox.create(
            aggregateType,
            aggregateId,
            type,
            payload.toJson()
        );

        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
    }
}

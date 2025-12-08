package com.nowayback.project.application.outbox.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.project.domain.outbox.Outbox;
import com.nowayback.project.domain.outbox.vo.AggregateType;
import com.nowayback.project.domain.outbox.vo.EventDestination;
import com.nowayback.project.domain.outbox.vo.EventPayload;
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

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(
        EventType type,
        EventDestination destination,
        EventPayload payload,
        AggregateType aggregateType,
        UUID aggregateId
    ) {
        try {
            String json = objectMapper.writeValueAsString(payload);

            Outbox outbox = Outbox.create(
                aggregateType,
                aggregateId,
                type,
                destination,
                json,
                payload.getClass().getName()
            );

            applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));

        } catch (Exception e) {
            throw new RuntimeException("Outbox 저장 중 오류", e);
        }
    }
}

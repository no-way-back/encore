package com.nowayback.project.application.outbox.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.project.application.event.EventPayload;
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

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(EventType type, EventPayload payload, AggregateType aggregateType, UUID aggregateId) {
        log.info(
            "[OutboxEventPublisher.publish] type={}, payload={}, AggregateType={}, aggregateId={}",
            type, payload, aggregateType, aggregateId);
        Outbox outbox = Outbox.create(
            aggregateType,
            aggregateId,
            type,
            toJson(payload)
        );

        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[EventPayload.toJson] EventPayload 직렬화에 실패했습니다.", e);
        }
    }
}

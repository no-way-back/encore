package com.nowayback.reward.application.outbox.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.reward.application.reward.dto.RewardCreationResult;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.outbox.Outbox;
import com.nowayback.reward.domain.outbox.vo.AggregateType;
import com.nowayback.reward.domain.outbox.vo.EventDestination;
import com.nowayback.reward.domain.outbox.vo.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.nowayback.reward.domain.exception.RewardErrorCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    public void publish(
            EventType eventType,
            EventDestination destination,
            RewardCreationResult result,
            AggregateType aggregateType,
            UUID aggregateId
    ) {
        try {
            String payloadJson = serializePayload(result);
            String payloadType = result.getClass().getName();

            Outbox outbox = Outbox.create(
                    aggregateType,
                    aggregateId,
                    eventType,
                    destination,
                    payloadJson,
                    payloadType
            );

            applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));

            log.debug("Outbox 이벤트 발행 - type: {}, aggregateId: {}",
                    eventType, aggregateId);

        } catch (Exception e) {
            log.error("Outbox 이벤트 발행 실패 - type: {}, aggregateId: {}",
                    eventType, aggregateId, e);
            throw new RewardException(OUTBOX_EVENT_PUBLISH_FAILED);
        }
    }

    private String serializePayload(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Payload 직렬화 실패: {}", payload.getClass().getName(), e);
            throw new RewardException(OUTBOX_PAYLOAD_SERIALIZATION_FAILED);
        }
    }
}
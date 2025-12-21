package com.nowayback.reward.application.outbox.event;

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

    public void publish(
            EventType eventType,
            EventDestination destination,
            RewardCreationResult result,
            AggregateType aggregateType,
            UUID aggregateId
    ) {
        try {
            Outbox outbox = Outbox.create(
                    aggregateType,
                    aggregateId,
                    eventType,
                    destination,
                    result
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
}
package com.nowayback.reward.infrastructure.kafka.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.reward.application.outbox.event.OutboxPublisher;
import com.nowayback.reward.application.outbox.repository.OutboxRepository;
import com.nowayback.reward.application.reward.dto.RewardCreationResult;
import com.nowayback.reward.domain.outbox.entity.Outbox;
import com.nowayback.reward.infrastructure.kafka.dto.project.event.RewardCreationResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxKafkaPublisher implements OutboxPublisher {

    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final int MAX_RETRY_COUNT = 5;
    private static final int KAFKA_TIMEOUT_SECONDS = 3;

    @Override
    public void publishImmediately(Outbox outbox) {
        try {
            sendToKafka(outbox);
            updatePublishedStatus(outbox);

            log.info("즉시 발행 성공 - outboxId: {}, topic: {}", outbox.getId(), outbox.getDestination().getTopicName());
        } catch (Exception e) {
            log.warn("즉시 발행 실패, 폴링에서 재시도 - outboxId: {}, error: {}", outbox.getId(), e.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean publishWithRetry(Outbox outbox) {
        if (outbox.getRetryCount() >= MAX_RETRY_COUNT) {
            log.error("재시도 한계 초과 - outboxId: {}, 수동 처리 필요", outbox.getId());

            outbox.markAsFailed("최대 재시도 횟수(" + MAX_RETRY_COUNT + "회) 초과");
            outboxRepository.save(outbox);

            return false;
        }

        try {
            sendToKafka(outbox);
            outbox.markAsPublished();
            outboxRepository.save(outbox);

            log.info("폴링 발행 성공 - outboxId: {}, retryCount: {}", outbox.getId(), outbox.getRetryCount());

            return true;

        } catch (Exception e) {
            outbox.incrementRetryCount();
            outboxRepository.save(outbox);

            log.warn("폴링 발행 실패 - outboxId: {}, retryCount: {}/{}, error: {}",
                    outbox.getId(),
                    outbox.getRetryCount(),
                    MAX_RETRY_COUNT,
                    e.getMessage());
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void updatePublishedStatus(Outbox outbox) {
        outbox.markAsPublished();
        outboxRepository.save(outbox);
    }

    private void sendToKafka(Outbox outbox) throws Exception {
        Object payload = deserializePayload(outbox);
        Object kafkaEvent = toKafkaEvent(payload);

        String topic = outbox.getDestination().getTopicName();
        String key = outbox.getAggregateId().toString();

        kafkaTemplate.send(topic, key, kafkaEvent)
                .get(KAFKA_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        log.debug("Kafka 발행 완료 - topic: {}, key: {}", topic, key);
    }

    /**
     * Outbox의 JSON payload를 객체로 역직렬화
     */
    private Object deserializePayload(Outbox outbox) throws Exception {
        Class<?> payloadClass = Class.forName(outbox.getPayloadType());
        return objectMapper.readValue(outbox.getPayloadJson(), payloadClass);
    }

    /**
     * Application DTO를 Kafka Event로 변환
     */
    private Object toKafkaEvent(Object payload) {
        if (payload instanceof RewardCreationResult result) {
            return RewardCreationResultEvent.from(result);
        }

        throw new IllegalArgumentException(
                "지원하지 않는 payload 타입: " + payload.getClass().getName()
        );
    }
}
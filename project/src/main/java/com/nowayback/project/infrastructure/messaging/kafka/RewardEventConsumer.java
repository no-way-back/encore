package com.nowayback.project.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.project.application.event.EventDispatcher;
import com.nowayback.project.application.projectdraft.event.payload.RewardCreateResultEventPayload;
import com.nowayback.project.domain.outbox.vo.EventType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardEventConsumer {

    private final ObjectMapper objectMapper;
    private final EventDispatcher eventDispatcher;

    @KafkaListener(topics = KafkaTopics.REWARD_CREATION_RESULT, groupId = "${spring.kafka.consumer.group-id}")
    public void onRewardCreationResult(String message, Acknowledgment acknowledgment)
        throws JsonProcessingException {
        try {
            log.info("[RewardEventConsumer.onRewardCreateResult] 리워드 생성 결과 이벤트 수신");
            RewardCreateResultEventPayload payload = objectMapper.readValue(
                message, RewardCreateResultEventPayload.class);
            String result = getResult(payload);

            if (payload.getPayload().isSuccess()) {
                onRewardCreated(result);
            } else {
                onRewardCreateFailed(result);
            }

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("[RewardEventConsumer.onRewardCreated] 리워드 생성 이벤트 처리 실패: {}", message, e);
            throw e;
        }
    }

    private void onRewardCreated(String message) {
        try {
            log.info("[RewardEventConsumer.onRewardCreated] 리워드 생성 이벤트 수신");

            eventDispatcher.dispatch(EventType.REWARD_CREATED, message);

        } catch (Exception e) {
            log.error("[RewardEventConsumer.onRewardCreated] 리워드 생성 이벤트 처리 실패: {}", message, e);
            throw e;
        }
    }

    private void onRewardCreateFailed(String message) {
        try {
            log.info("[RewardEventConsumer.onRewardCreated] 리워드 생성 실패 이벤트 수신");

            eventDispatcher.dispatch(EventType.REWARD_CREATION_FAILED, message);

        } catch (Exception e) {
            log.error("[RewardEventConsumer.onRewardCreated] 리워드 생성 실패 이벤트 처리 실패: {}", message, e);
            throw e;
        }
    }

    private String getResult(RewardCreateResultEventPayload payload)
        throws JsonProcessingException {
        return objectMapper.writeValueAsString(new Result(payload.getPayload().getProjectId()));
    }

    public record Result(UUID projectId) {

    }
}

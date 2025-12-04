package com.nowayback.project.infrastructure.messaging.kafka;

import com.nowayback.project.application.event.EventDispatcher;
import com.nowayback.project.domain.outbox.vo.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardEventConsumer {

    private final EventDispatcher eventDispatcher;

    @KafkaListener(topics = KafkaTopics.REWARD_CREATED, groupId = "${spring.kafka.consumer.group-id}")
    public void onRewardCreated(String message, Acknowledgment ack) {
        try {
            log.info("[RewardEventConsumer.onRewardCreated] 리워드 생성 이벤트 수신");

            eventDispatcher.dispatch(EventType.REWARD_CREATED, message);

            ack.acknowledge();

        } catch (Exception e) {
            log.error("[RewardEventConsumer.onRewardCreated] 리워드 생성 이벤트 처리 실패: {}", message, e);
            throw e;
        }
    }

    @KafkaListener(topics = KafkaTopics.REWARD_CREATION_FAILED, groupId = "${spring.kafka.consumer.group-id}")
    public void onRewardCreateFailed(String message, Acknowledgment ack) {
        try {
            log.info("[RewardEventConsumer.onRewardCreated] 리워드 생성 실패 이벤트 수신");

            eventDispatcher.dispatch(EventType.REWARD_CREATION_FAILED, message);

            ack.acknowledge();

        } catch (Exception e) {
            log.error("[RewardEventConsumer.onRewardCreated] 리워드 생성 실패 이벤트 처리 실패: {}", message, e);
            throw e;
        }
    }
}

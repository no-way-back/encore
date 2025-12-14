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
public class FundingEventConsumer {

    private final EventDispatcher eventDispatcher;

    @KafkaListener(topics = KafkaTopics.FUNDING_CREATION_FAILED, groupId = "${spring.kafka.consumer.group-id}")
    public void onFundingCreateFailed(String message, Acknowledgment ack) {
        try {
            log.info("[FundingEventConsumer.onFundingCreateFailed] 펀딩 생성 실패 이벤트 수신 message={}", message);
            eventDispatcher.dispatch(EventType.FUNDING_CREATION_FAILED, message);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("[FundingEventConsumer.onFundingCreateFailed] 펀딩 생성 실패 이벤트 처리 실패 message = {}", message, e);
            throw e;
        }
    }

    @KafkaListener(topics = KafkaTopics.PROJECT_FUNDING_SUCCESS, groupId = "${spring.kafka.consumer.group-id}")
    public void onFundingSuccess(String message, Acknowledgment ack) {
        try {
            log.info("[FundingEventConsumer.onFundingSuccess] 펀딩 성공 이벤트 수신 message={}", message);
            eventDispatcher.dispatch(EventType.PROJECT_FUNDING_SUCCEEDED, message);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("[FundingEventConsumer.onFundingSuccess] 펀딩 성공 이벤트 처리 실패 message = {}", message, e);
            throw e;
        }
    }

    @KafkaListener(topics = KafkaTopics.PROJECT_FUNDING_FAILED, groupId = "${spring.kafka.consumer.group-id}")
    public void onFundingFailed(String message, Acknowledgment ack) {
        try {
            log.info("[FundingEventConsumer.onFundingFailed] 펀딩 실패 이벤트 수신 message={}", message);
            eventDispatcher.dispatch(EventType.PROJECT_FUNDING_FAILED, message);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("[FundingEventConsumer.onFundingFailed] 펀딩 실패 이벤트 처리 실패 message = {}", message, e);
            throw e;
        }
    }
}

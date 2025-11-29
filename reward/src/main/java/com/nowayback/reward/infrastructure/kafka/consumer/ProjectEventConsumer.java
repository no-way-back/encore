package com.nowayback.reward.infrastructure.kafka.consumer;

import com.nowayback.reward.domain.reward.handler.ProjectEventHandler;
import com.nowayback.reward.domain.reward.handler.command.RewardCreateCommand;
import com.nowayback.reward.infrastructure.kafka.dto.project.data.RewardCreateData;
import com.nowayback.reward.infrastructure.kafka.dto.project.event.ProjectCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventConsumer {

    private final ProjectEventHandler projectEventHandler;

    @KafkaListener(
            topics = "${spring.kafka.topic.project-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeProjectEvent(
            @Payload ProjectCreatedEvent event,
            Acknowledgment acknowledgment
    ) {
        log.info("이벤트 수신 - ID: {}, 타입: {}, 프로젝트: {}",
                event.eventId(),
                event.eventType(),
                event.payload().projectId());

        if (!"PROJECT_CREATED".equals(event.eventType())) {
            log.warn("처리 불가능한 이벤트 타입: {}", event.eventType());
            acknowledgment.acknowledge();
            return;
        }

        List<RewardCreateCommand> rewardCommands = RewardCreateData.toCommands(
                event.payload().rewards()
        );

        projectEventHandler.handle(
                event.payload().projectId(),
                event.payload().creatorId(),
                rewardCommands
        );

        log.info("리워드 생성 완료 - 프로젝트: {}", event.payload().projectId());
        acknowledgment.acknowledge();
    }
}
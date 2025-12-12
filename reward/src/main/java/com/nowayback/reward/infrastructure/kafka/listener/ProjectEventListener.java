package com.nowayback.reward.infrastructure.kafka.listener;

import com.nowayback.reward.application.outbox.event.OutboxEventPublisher;
import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.application.reward.command.RewardCreateCommand;
import com.nowayback.reward.application.reward.dto.RewardCreationResult;
import com.nowayback.reward.domain.outbox.vo.AggregateType;
import com.nowayback.reward.domain.outbox.vo.EventDestination;
import com.nowayback.reward.domain.outbox.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.project.data.RewardCreateData;
import com.nowayback.reward.infrastructure.kafka.dto.project.event.ProjectCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventListener {

    private final RewardService rewardService;
    private final OutboxEventPublisher outboxEventPublisher;

    @KafkaListener(
            topics = "${spring.kafka.topic.project-reward-creation}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "projectCreatedListenerFactory"
    )
    public void consumeProjectEvent(
            @Payload ProjectCreatedEvent event,
            Acknowledgment acknowledgment
    ) {
        log.info("이벤트 수신 - ID: {}, 타입: {}, 프로젝트: {}",
                event.eventId(),
                event.eventType(),
                event.payload().projectId()
        );

        if (!EventType.PROJECT_CREATED.equals(event.eventType())) {
            log.warn("처리 불가능한 이벤트 타입: {}", event.eventType());
            acknowledgment.acknowledge();
            return;
        }

        UUID projectId = event.payload().projectId();

        try {
            List<RewardCreateCommand> rewardCommands = RewardCreateData.toCommands(
                    event.payload().rewards()
            );

            rewardService.createRewardsForProject(
                    event.eventId(),
                    projectId,
                    event.payload().creatorId(),
                    rewardCommands
            );

            log.info("리워드 생성 완료 - 프로젝트: {}", projectId);

            RewardCreationResult result = RewardCreationResult.success(projectId);

            outboxEventPublisher.publish(
                    EventType.REWARD_CREATION_SUCCESS,
                    EventDestination.PROJECT_SERVICE,
                    result,
                    AggregateType.PROJECT,
                    projectId
            );

        } catch (Exception e) {
            log.error("리워드 생성 실패 - 프로젝트: {}", projectId, e);

            RewardCreationResult result = RewardCreationResult.failure(projectId);

            outboxEventPublisher.publish(
                    EventType.REWARD_CREATION_FAILED,
                    EventDestination.PROJECT_SERVICE,
                    result,
                    AggregateType.PROJECT,
                    projectId
            );

        } finally {
            acknowledgment.acknowledge();
        }
    }
}
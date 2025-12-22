package com.nowayback.reward.infrastructure.kafka.listener;

import com.nowayback.reward.application.inbox.InboxProcessor;
import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.application.reward.command.RewardCreateCommand;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventListener {

    private final RewardService rewardService;
    private final InboxProcessor inboxProcessor;

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

        try {
            inboxProcessor.processEvent(
                    event.eventId(),
                    event.eventType(),
                    event.payload().projectId(),
                    event.payload(),
                    payload -> {

                        List<RewardCreateCommand> rewardCommands =
                                RewardCreateData.toCommands(payload.rewards());

                        rewardService.createRewardsForProject(
                                event.eventId(),
                                payload.projectId(),
                                payload.creatorId(),
                                rewardCommands
                        );
                    }
            );

            acknowledgment.acknowledge();

            log.info("리워드 생성 완료 - 프로젝트: {}", event.payload().projectId());

        } catch (Exception e) {
            log.error("리워드 생성 실패 - 프로젝트: {}", event.payload().projectId(), e);
            throw e;
        }
    }
}
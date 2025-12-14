package com.nowayback.project.application.projectdraft.event;

import com.nowayback.project.application.event.EventHandler;
import com.nowayback.project.application.outbox.event.OutboxEventPublisher;
import com.nowayback.project.application.project.ProjectService;
import com.nowayback.project.application.projectdraft.event.payload.FundingCreateFailedEventPayload;
import com.nowayback.project.application.projectdraft.event.payload.RewardCreationCompensationEventPayload;
import com.nowayback.project.domain.outbox.vo.AggregateType;
import com.nowayback.project.domain.outbox.vo.EventDestination;
import com.nowayback.project.domain.outbox.vo.EventType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FundingCreateFailedEventHandler implements EventHandler<FundingCreateFailedEventPayload> {

    private final ProjectService projectService;
    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    public boolean supports(EventType eventType) {
        return EventType.FUNDING_CREATION_FAILED == eventType;
    }

    @Override
    public Class<FundingCreateFailedEventPayload> getPayloadType() {
        return FundingCreateFailedEventPayload.class;
    }

    @Override
    public void handle(FundingCreateFailedEventPayload payload) {
        UUID projectId = payload.getProjectId();

        projectService.markAsCreationFailed(
            payload.getProjectId(),
            payload.getFailureReason()
        );

        publishRewardCompensationEvent(projectId);
    }

    private void publishRewardCompensationEvent(UUID projectId) {
        outboxEventPublisher.publish(
            EventType.REWARD_CREATION_COMPENSATION,
            EventDestination.KAFKA,
            RewardCreationCompensationEventPayload.from(projectId),
            AggregateType.PROJECT,
            projectId
        );
    }
}

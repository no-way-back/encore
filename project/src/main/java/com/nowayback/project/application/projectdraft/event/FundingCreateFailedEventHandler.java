package com.nowayback.project.application.projectdraft.event;

import com.nowayback.project.application.event.EventHandler;
import com.nowayback.project.application.outbox.event.OutboxEventPublisher;
import com.nowayback.project.application.projectdraft.event.payload.FundingCreateFailedEventPayload;
import com.nowayback.project.application.projectdraft.event.payload.RewardCreationCompensationEventPayload;
import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.outbox.vo.AggregateType;
import com.nowayback.project.domain.outbox.vo.EventDestination;
import com.nowayback.project.domain.outbox.vo.EventType;
import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.repository.ProjectRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FundingCreateFailedEventHandler implements EventHandler<FundingCreateFailedEventPayload> {

    private final ProjectRepository projectRepository;
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
    @Transactional
    public void handle(FundingCreateFailedEventPayload payload) {
        UUID projectId = payload.getProjectId();

        Project project = readProject(projectId);
        project.markAsCreationFailed(payload.getFailureReason());

        publishRewardCompensationEvent(projectId);
    }

    private Project readProject(UUID projectId) {
        return projectRepository.findById(projectId).orElseThrow(
            () -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND)
        );
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

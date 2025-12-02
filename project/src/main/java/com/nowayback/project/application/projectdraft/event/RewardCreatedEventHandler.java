package com.nowayback.project.application.projectdraft.event;

import com.nowayback.project.application.event.EventHandler;
import com.nowayback.project.application.outbox.event.OutboxEventPublisher;
import com.nowayback.project.application.projectdraft.dto.ProjectFundingDraftResult;
import com.nowayback.project.application.projectdraft.event.payload.FundingCreationEventPayload;
import com.nowayback.project.application.projectdraft.event.payload.RewardCreatedEventPayload;
import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.outbox.vo.AggregateType;
import com.nowayback.project.domain.outbox.vo.EventDestination;
import com.nowayback.project.domain.outbox.vo.EventType;
import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.repository.ProjectRepository;
import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.repository.ProjectDraftRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class RewardCreatedEventHandler implements EventHandler<RewardCreatedEventPayload> {

    private final ProjectRepository projectRepository;
    private final ProjectDraftRepository projectDraftRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    public boolean supports(EventType eventType) {
        return eventType == EventType.REWARD_CREATED;
    }

    @Override
    public Class<RewardCreatedEventPayload> getPayloadType() {
        return RewardCreatedEventPayload.class;
    }

    @Override
    public void handle(RewardCreatedEventPayload payload) {
        log.info("[RewardCreatedEventHandler.handle] 이벤트 수신 - payload: {}", payload);
        UUID projectId = payload.getProjectId();

        Project project = readProject(projectId);
        ProjectDraft projectDraft = readProjectDraft(project.getProjectDraftId());
        validateFundingDraft(projectDraft);

        publishFundingCreationEvent(project, projectDraft);
    }

    private Project readProject(UUID projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));
    }

    private ProjectDraft readProjectDraft(UUID projectDraftId) {
        return projectDraftRepository.findById(projectDraftId)
            .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_DRAFT_NOT_FOUND));
    }

    private void validateFundingDraft(ProjectDraft projectDraft) {
        if (projectDraft.getFundingDraft() == null) {
            throw new ProjectException(ProjectErrorCode.FUNDING_DRAFT_NOT_FOUND);
        }
    }

    private void publishFundingCreationEvent(Project project, ProjectDraft projectDraft) {
        outboxEventPublisher.publish(
            EventType.PROJECT_FUNDING_CREATION,
            EventDestination.KAFKA,
            FundingCreationEventPayload.from(
                project.getId(),
                projectDraft.getUserId(),
                ProjectFundingDraftResult.of(projectDraft)
            ),
            AggregateType.PROJECT,
            project.getId()
        );
    }
}

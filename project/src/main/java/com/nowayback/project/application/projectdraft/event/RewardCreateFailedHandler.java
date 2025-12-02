package com.nowayback.project.application.projectdraft.event;

import com.nowayback.project.application.event.EventHandler;
import com.nowayback.project.application.projectdraft.event.payload.RewardCreateFailedEventPayload;
import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.outbox.vo.EventType;
import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.repository.ProjectRepository;
import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.repository.ProjectDraftRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardCreateFailedHandler implements EventHandler<RewardCreateFailedEventPayload> {

    private final ProjectRepository projectRepository;
    private final ProjectDraftRepository projectDraftRepository;

    @Override
    public boolean supports(EventType eventType) {
        return EventType.REWARD_CREATION_FAILED == eventType;
    }

    @Override
    public Class<RewardCreateFailedEventPayload> getPayloadType() {
        return RewardCreateFailedEventPayload.class;
    }

    @Override
    @Transactional
    public void handle(RewardCreateFailedEventPayload payload) {
        log.info("[RewardCreateFailedHandler.handle] 이벤트 수신 payload: {}", payload);
        Project project = readProject(payload.getProjectId());
        // TODO
        project.markAsCreationFailed("RewardCreateFailed");

        ProjectDraft draft =readProjectDraft(project.getProjectDraftId());
        draft.markAsDraft();
    }

    private Project readProject(UUID projectId) {
        return  projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));
    }

    private ProjectDraft readProjectDraft(UUID projectDraftId) {
        return projectDraftRepository.findById(projectDraftId)
            .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_DRAFT_NOT_FOUND));
    }
}

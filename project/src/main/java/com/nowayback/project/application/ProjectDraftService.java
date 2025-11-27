package com.nowayback.project.application;

import com.nowayback.project.application.command.SaveFundingDraftCommand;
import com.nowayback.project.application.command.SaveSettlementDraftCommand;
import com.nowayback.project.application.command.SaveStoryDraftCommand;
import com.nowayback.project.application.dto.ProjectFundingDraftResult;
import com.nowayback.project.application.dto.ProjectSettlementDraftResult;
import com.nowayback.project.application.dto.ProjectStoryDraftResult;
import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.repository.ProjectDraftRepository;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectDraftService {

    private final ProjectDraftRepository projectDraftRepository;

    @Transactional
    public UUID createProjectDraft(UUID userId) {
        ProjectDraft projectDraft = ProjectDraft.create(userId);

        return projectDraftRepository.save(projectDraft).getId();
    }

    @Transactional
    public ProjectStoryDraftResult saveStoryDraft(SaveStoryDraftCommand command) {
        ProjectDraft projectDraft = findUpdateAbleDraftOrThrow(command.projectDraftId());

        projectDraft.updateStoryDraft(
            command.title(),
            command.summary(),
            command.category(),
            command.thumbnailUrl(),
            command.contentJson()
        );

        return ProjectStoryDraftResult.of(projectDraft);
    }

    @Transactional
    public ProjectFundingDraftResult saveFundingDraft(SaveFundingDraftCommand command) {
        ProjectDraft projectDraft = findUpdateAbleDraftOrThrow(command.projectDraftId());

        projectDraft.updateFundingDraft(
            command.goalAmount(),
            command.fundingStartDate(),
            command.fundingEndDate()
        );

        return ProjectFundingDraftResult.of(projectDraft);
    }

    @Transactional
    public ProjectSettlementDraftResult saveSettlementDraft(SaveSettlementDraftCommand command) {
        ProjectDraft projectDraft = findUpdateAbleDraftOrThrow(command.projectDraftId());

        projectDraft.updateSettlementDraft(
            command.businessNumber(),
            command.accountBank(),
            command.accountNumber(),
            command.accountHolder()
        );

        return ProjectSettlementDraftResult.of(projectDraft);
    }

    private ProjectDraft findUpdateAbleDraftOrThrow(UUID projectDraftId) {
        return projectDraftRepository.findByIdAndStatus(
            projectDraftId,
            ProjectDraftStatus.DRAFT
        ).orElseThrow(() -> new IllegalArgumentException());
    }
}

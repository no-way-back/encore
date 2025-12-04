package com.nowayback.project.application.projectdraft.dto;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import java.util.UUID;

public record ProjectDraftResult(
    UUID userId,
    ProjectFundingDraftResult fundingDraftResult,
    ProjectRewardDraftResult rewardDraftResult,
    ProjectSettlementDraftResult settlementDraftResult,
    ProjectStoryDraftResult storyDraftResult
) {
    public static ProjectDraftResult of(ProjectDraft projectDraft) {
        return new ProjectDraftResult(
            projectDraft.getUserId(),
            ProjectFundingDraftResult.of(projectDraft),
            ProjectRewardDraftResult.of(projectDraft),
            ProjectSettlementDraftResult.of(projectDraft),
            ProjectStoryDraftResult.of(projectDraft)
        );
    }
}

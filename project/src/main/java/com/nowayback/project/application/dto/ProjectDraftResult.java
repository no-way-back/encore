package com.nowayback.project.application.dto;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;

public record ProjectDraftResult(
    ProjectFundingDraftResult fundingDraftResult,
    ProjectRewardDraftResult rewardDraftResult,
    ProjectSettlementDraftResult settlementDraftResult,
    ProjectStoryDraftResult storyDraftResult
) {
    public static ProjectDraftResult of(ProjectDraft projectDraft) {
        return new ProjectDraftResult(
            ProjectFundingDraftResult.of(projectDraft),
            ProjectRewardDraftResult.of(projectDraft),
            ProjectSettlementDraftResult.of(projectDraft),
            ProjectStoryDraftResult.of(projectDraft)
        );
    }
}

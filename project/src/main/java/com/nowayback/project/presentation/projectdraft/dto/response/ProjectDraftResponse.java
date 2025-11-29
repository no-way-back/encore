package com.nowayback.project.presentation.projectdraft.dto.response;

import com.nowayback.project.application.projectdraft.dto.ProjectDraftResult;

public record ProjectDraftResponse(
    ProjectStoryDraftResponse storyDraft,
    ProjectFundingDraftResponse fundingDraft,
    ProjectRewardDraftResponse rewardDraft,
    ProjectSettlementDraftResponse settlementDraft
) {

    public static ProjectDraftResponse of(
        ProjectDraftResult result
    ) {
        return new ProjectDraftResponse(
            ProjectStoryDraftResponse.from(result.storyDraftResult()),
            ProjectFundingDraftResponse.from(result.fundingDraftResult()),
            ProjectRewardDraftResponse.from(result.rewardDraftResult()),
            ProjectSettlementDraftResponse.from(result.settlementDraftResult())
        );
    }
}

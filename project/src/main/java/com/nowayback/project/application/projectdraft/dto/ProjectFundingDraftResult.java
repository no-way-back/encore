package com.nowayback.project.application.projectdraft.dto;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectFundingDraftResult(
    UUID projectDraftId,
    Long goalAmount,
    LocalDate fundingStartDate,
    LocalDate fundingEndDate
) {

    public static ProjectFundingDraftResult of(ProjectDraft projectDraft) {
        return new ProjectFundingDraftResult(
            projectDraft.getId(),
            projectDraft.getFundingDraft().getGoalAmount(),
            projectDraft.getFundingDraft().getFundingStartDate(),
            projectDraft.getFundingDraft().getFundingEndDate()
        );
    }
}

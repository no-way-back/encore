package com.nowayback.project.presentation.projectdraft.dto.response;

import com.nowayback.project.application.dto.ProjectFundingDraftResult;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectFundingDraftResponse(
    UUID projectDraftId,
    Long goalAmount,
    LocalDate fundingStartDate,
    LocalDate fundingEndDate
) {

    public static ProjectFundingDraftResponse from(ProjectFundingDraftResult result) {
        return new ProjectFundingDraftResponse(
            result.projectDraftId(),
            result.goalAmount(),
            result.fundingStartDate(),
            result.fundingEndDate()
        );
    }
}

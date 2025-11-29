package com.nowayback.project.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record SaveFundingDraftCommand(
    UUID projectDraftId,
    Long goalAmount,
    LocalDate fundingStartDate,
    LocalDate fundingEndDate
) {
    public static SaveFundingDraftCommand of(
        UUID projectDraftId,
        Long goalAmount,
        LocalDate fundingStartDate,
        LocalDate fundingEndDate
    ) {
        return new SaveFundingDraftCommand(projectDraftId, goalAmount, fundingStartDate, fundingEndDate);
    }
}

package com.nowayback.project.presentation.projectdraft.dto.request;

import com.nowayback.project.application.command.SaveFundingDraftCommand;
import java.time.LocalDate;
import java.util.UUID;

public record SaveProjectFundingDraftRequest(
    Long goalAmount,
    LocalDate fundingStartDate,
    LocalDate fundingEndDate
) {

    public SaveFundingDraftCommand toCommand(UUID draftId, UUID userId) {
        return SaveFundingDraftCommand.of(
            draftId,
            goalAmount,
            fundingStartDate,
            fundingEndDate
        );
    }
}

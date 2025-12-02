package com.nowayback.project.presentation.projectdraft.dto.request;

import com.nowayback.project.application.projectdraft.command.SaveSettlementDraftCommand;
import java.util.UUID;

public record SaveProjectSettlementDraft(
    String businessNumber,
    String accountBank,
    String accountNumber,
    String accountHolder
) {

    public SaveSettlementDraftCommand toCommand(UUID draftId, UUID userId) {
        return SaveSettlementDraftCommand.of(
            draftId,
            businessNumber,
            accountBank,
            accountNumber,
            accountHolder
        );
    }
}

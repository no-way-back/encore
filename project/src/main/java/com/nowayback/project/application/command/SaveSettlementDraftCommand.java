package com.nowayback.project.application.command;

import java.util.UUID;

public record SaveSettlementDraftCommand(
    UUID projectDraftId,
    String businessNumber,
    String accountBank,
    String accountNumber,
    String accountHolder
) {

    public static SaveSettlementDraftCommand of(
        UUID draftId,
        String businessNumber,
        String accountBank,
        String accountNumber,
        String accountHolder
    ) {
        return new SaveSettlementDraftCommand(
            draftId,
            businessNumber,
            accountBank,
            accountNumber,
            accountHolder
        );
    }
}

package com.nowayback.project.presentation.project.dto.response;

import com.nowayback.project.application.project.dto.SettlementResult;
import java.util.UUID;

public record SettlementResponse(
    UUID projectId,
    String accountBank,
    String accountNumber,
    String accountHolderName
) {

    public static SettlementResponse from(SettlementResult result) {
        return new SettlementResponse(
            result.projectId(),
            result.accountBank(),
            result.accountNumber(),
            result.accountHolderName()
        );
    }
}

package com.nowayback.project.presentation.projectdraft.dto.response;

import com.nowayback.project.application.dto.ProjectSettlementDraftResult;
import java.util.UUID;

public record ProjectSettlementDraftResponse(
    UUID projectDraftId,
    String businessNumber,
    String accountBank,
    String accountNumber,
    String accountHolder
) {

    public static ProjectSettlementDraftResponse from(ProjectSettlementDraftResult result) {
        return new ProjectSettlementDraftResponse(
            result.projectDraftId(),
            result.businessNumber(),
            result.accountBank(),
            result.accountNumber(),
            result.accountHolder()
        );
    }
}

package com.nowayback.project.application.dto;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import java.util.UUID;

public record ProjectSettlementDraftResult(
    UUID projectDraftId,
    String businessNumber,
    String accountBank,
    String accountNumber,
    String accountHolder
) {
    public static ProjectSettlementDraftResult of(ProjectDraft projectDraft) {
        return new ProjectSettlementDraftResult(
            projectDraft.getId(),
            projectDraft.getSettlementDraft().getBusinessNumber(),
            projectDraft.getSettlementDraft().getAccountBank(),
            projectDraft.getSettlementDraft().getAccountNumber(),
            projectDraft.getSettlementDraft().getAccountHolder()
        );
    }
}

package com.nowayback.project.application.project.dto;


import com.nowayback.project.domain.project.entity.Project;
import java.util.UUID;

public record SettlementResult(
    UUID projectId,
    String accountBank,
    String accountNumber,
    String accountHolderName
) {
    public static SettlementResult from(Project project) {
        return new SettlementResult(
            project.getId(),
            project.getAccount().getAccountBank(),
            project.getAccount().getAccountNumber(),
            project.getAccount().getAccountHolderName()
        );
    }
}

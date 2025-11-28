package com.nowayback.project.presentation.projectdraft.dto.request;

public record SaveProjectSettlementDraft(
    String businessNumber,
    String accountBank,
    String accountNumber,
    String accountHolder
) {

}

package com.nowayback.payment.infrastructure.settlement.external.openbanking.dto;

public record OpenBankingTransferRequest(
        String accountBank,
        String accountNumber,
        String accountHolderName,
        Long amount
) {
}

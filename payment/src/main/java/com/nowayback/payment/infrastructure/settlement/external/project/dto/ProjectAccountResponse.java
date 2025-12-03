package com.nowayback.payment.infrastructure.settlement.external.project.dto;

import java.util.UUID;

public record ProjectAccountResponse (
        UUID projectId,
        String accountBank,
        String accountNumber,
        String accountHolderName
) {
}

package com.nowayback.payment.application.settlement.service.project.dto;

import java.util.UUID;

public record ProjectAccountResult (
        UUID projectId,
        String accountBank,
        String accountNumber,
        String accountHolderName
) {
}

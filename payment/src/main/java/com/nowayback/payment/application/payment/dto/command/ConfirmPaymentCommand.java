package com.nowayback.payment.application.payment.dto.command;

import com.nowayback.payment.domain.payment.vo.*;

import java.util.UUID;

public record ConfirmPaymentCommand (
        UserId userId,
        FundingId fundingId,
        ProjectId projectId,
        Money amount,
        PgInfo pgInfo
) {

    public static ConfirmPaymentCommand of(
            UUID userId,
            UUID fundingId,
            UUID projectId,
            Long amount,
            String pgMethod,
            String pgTransactionId,
            String pgOrderId
    ) {
        return new ConfirmPaymentCommand(
                UserId.of(userId),
                FundingId.of(fundingId),
                ProjectId.of(projectId),
                Money.of(amount),
                PgInfo.of(pgMethod, pgTransactionId, pgOrderId)
        );
    }
}

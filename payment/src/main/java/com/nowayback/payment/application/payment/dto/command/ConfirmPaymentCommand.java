package com.nowayback.payment.application.payment.dto.command;

import com.nowayback.payment.domain.payment.vo.*;

import java.util.UUID;

public record ConfirmPaymentCommand (
        FundingId fundingId,
        PgInfo pgInfo
) {

    public static ConfirmPaymentCommand of(
            UUID fundingId,
            String pgMethod,
            String pgTransactionId,
            String pgOrderId
    ) {
        return new ConfirmPaymentCommand(
                FundingId.of(fundingId),
                PgInfo.of(pgMethod, pgTransactionId, pgOrderId)
        );
    }
}

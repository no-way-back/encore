package com.nowayback.payment.application.payment.dto.command;

import com.nowayback.payment.domain.payment.vo.FundingId;
import com.nowayback.payment.domain.payment.vo.Money;
import com.nowayback.payment.domain.payment.vo.PgInfo;
import com.nowayback.payment.domain.payment.vo.UserId;

import java.util.UUID;

public record ConfirmPaymentCommand (
        UserId userId,
        FundingId fundingId,
        Money amount,
        PgInfo pgInfo
) {

    public static ConfirmPaymentCommand of(
            UUID userId,
            UUID fundingId,
            Long amount,
            String pgMethod,
            String pgTransactionId,
            String pgOrderId
    ) {
        return new ConfirmPaymentCommand(
                UserId.of(userId),
                FundingId.of(fundingId),
                Money.of(amount),
                PgInfo.of(pgMethod, pgTransactionId, pgOrderId)
        );
    }
}

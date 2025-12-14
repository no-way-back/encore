package com.nowayback.payment.application.payment.dto.command;

import com.nowayback.payment.domain.payment.vo.FundingId;
import com.nowayback.payment.domain.payment.vo.Money;
import com.nowayback.payment.domain.payment.vo.ProjectId;
import com.nowayback.payment.domain.payment.vo.UserId;

import java.util.UUID;

public record CreatePaymentCommand (
        UserId userId,
        FundingId fundingId,
        ProjectId projectId,
        Money amount
) {

    public static CreatePaymentCommand of(
            UUID userId,
            UUID fundingId,
            UUID projectId,
            Long amount
    ) {
        return new CreatePaymentCommand(
                UserId.of(userId),
                FundingId.of(fundingId),
                ProjectId.of(projectId),
                Money.of(amount)
        );
    }
}

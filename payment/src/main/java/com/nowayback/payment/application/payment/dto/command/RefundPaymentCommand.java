package com.nowayback.payment.application.payment.dto.command;

import com.nowayback.payment.domain.payment.vo.FundingId;
import com.nowayback.payment.domain.payment.vo.RefundAccountInfo;

import java.util.UUID;

public record RefundPaymentCommand (
        FundingId fundingId,
        String cancelReason,
        RefundAccountInfo refundAccountInfo
) {

    public static RefundPaymentCommand of(
            UUID fundingId,
            String cancelReason,
            String refundAccountBank,
            String refundAccountNumber,
            String refundAccountHolderName
    ) {
        RefundAccountInfo info = null;

        if (refundAccountBank != null && refundAccountNumber != null && refundAccountHolderName != null) {
            info = RefundAccountInfo.of(
                    refundAccountBank,
                    refundAccountNumber,
                    refundAccountHolderName
            );
        }

        return new RefundPaymentCommand(
                FundingId.of(fundingId),
                cancelReason,
                info
        );
    }
}

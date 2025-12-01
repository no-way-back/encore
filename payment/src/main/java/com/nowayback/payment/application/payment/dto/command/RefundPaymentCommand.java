package com.nowayback.payment.application.payment.dto.command;

import com.nowayback.payment.domain.payment.vo.RefundAccountInfo;

import java.util.UUID;

public record RefundPaymentCommand (
        UUID paymentId,
        String cancelReason,
        RefundAccountInfo refundAccountInfo
) {

    public static RefundPaymentCommand of(
            UUID paymentId,
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
                paymentId,
                cancelReason,
                info
        );
    }
}

package com.nowayback.payment.presentation.payment.dto.response;

import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundPaymentResponse (
        UUID paymentId,
        UUID userId,
        UUID fundingId,
        Long amount,
        PaymentStatus status,
        String pgPaymentKey,
        String pgOrderId,
        String pgMethod,
        String refundAccountBank,
        String refundAccountNumber,
        String refundAccountHolderName,
        LocalDateTime approvedAt
) {

    public static RefundPaymentResponse from(PaymentResult paymentResult) {
        return new RefundPaymentResponse(
                paymentResult.paymentId(),
                paymentResult.userId(),
                paymentResult.fundingId(),
                paymentResult.amount(),
                paymentResult.status(),
                paymentResult.pgPaymentKey(),
                paymentResult.pgOrderId(),
                paymentResult.pgMethod(),
                paymentResult.refundAccountBank() != null ? paymentResult.refundAccountBank() : null,
                paymentResult.refundAccountNumber() != null ? paymentResult.refundAccountNumber() : null,
                paymentResult.refundAccountHolderName() != null ? paymentResult.refundAccountHolderName() : null,
                paymentResult.approvedAt()
        );
    }
}

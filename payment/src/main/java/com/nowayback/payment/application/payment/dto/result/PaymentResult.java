package com.nowayback.payment.application.payment.dto.result;

import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResult (
        UUID paymentId,
        UUID userId,
        UUID fundingId,
        UUID projectId,
        Long amount,
        PaymentStatus status,
        String pgMethod,
        String pgPaymentKey,
        String pgOrderId,
        String refundAccountBank,
        String refundAccountNumber,
        String refundAccountHolderName,
        String refundReason,
        LocalDateTime approvedAt,
        LocalDateTime refundedAt
) {

    public static PaymentResult from(Payment payment) {
        return new PaymentResult(
                payment.getId(),
                payment.getUserId().getId(),
                payment.getFundingId().getId(),
                payment.getProjectId().getId(),
                payment.getAmount().getAmount(),
                payment.getStatus(),
                payment.getPgInfo() != null ? payment.getPgInfo().getPgMethod() : null,
                payment.getPgInfo() != null ? payment.getPgInfo().getPgPaymentKey() : null,
                payment.getPgInfo() != null ? payment.getPgInfo().getPgOrderId() : null,
                payment.getRefundAccountInfo() != null ? payment.getRefundAccountInfo().getRefundAccountBank() : null,
                payment.getRefundAccountInfo() != null ? payment.getRefundAccountInfo().getRefundAccountNumber() : null,
                payment.getRefundAccountInfo() != null ? payment.getRefundAccountInfo().getRefundAccountHolderName() : null,
                payment.getRefundReason(),
                payment.getApprovedAt(),
                payment.getRefundedAt()
        );
    }
}

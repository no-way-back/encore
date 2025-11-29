package com.nowayback.payment.presentation.payment.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RefundPaymentRequest (
        @NotNull(message = "펀딩 ID는 필수 값입니다.") UUID fundingId,
        String reason,
        String refundAccountBank,
        String refundAccountNumber,
        String refundAccountHolderName
) {
}

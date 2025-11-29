package com.nowayback.payment.presentation.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConfirmPaymentRequest (
        @NotNull(message = "펀딩 ID는 필수 값입니다.")
        UUID fundingId,

        @NotNull(message = "결제 금액은 필수 값입니다.")
        Long amount,

        @NotBlank(message = "PG 결제 키는 필수 값입니다.")
        String pgPaymentKey,

        @NotBlank(message = "PG 트랜잭션 ID는 필수 값입니다.")
        String pgTransactionId,

        @NotBlank(message = "PG 주문 번호는 필수 값입니다.")
        String pgOrderId,

        @NotBlank(message = "PG 결제 방식은 필수 값입니다.")
        String pgMethod
) {
}

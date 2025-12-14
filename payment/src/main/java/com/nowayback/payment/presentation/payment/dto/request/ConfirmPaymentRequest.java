package com.nowayback.payment.presentation.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "결제 승인 요청 정보")
public record ConfirmPaymentRequest (

        @Schema(description = "펀딩 ID", example = "00000000-0000-0000-0000-000000000000")
        @NotNull(message = "펀딩 ID는 필수 값입니다.")
        UUID fundingId,

        @NotBlank(message = "PG 결제 키는 필수 값입니다.")
        String pgPaymentKey,

        @Schema(description = "PG 주문 번호", example = "pg_order_id_123456")
        @NotBlank(message = "PG 주문 번호는 필수 값입니다.")
        String pgOrderId,

        @Schema(description = "PG 결제 방식", example = "CARD")
        @NotBlank(message = "PG 결제 방식은 필수 값입니다.")
        String pgMethod
) {
}

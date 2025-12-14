package com.nowayback.payment.presentation.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "결제 환불 요청 정보")
public record RefundPaymentRequest (

        @Schema(description = "결제 ID", example = "00000000-0000-0000-0000-000000000000")
        @NotNull(message = "결제 ID는 필수 값입니다.")
        UUID paymentId,

        @Schema(description = "환불 사유", example = "고객 요청에 의한 환불")
        String reason,

        @Schema(description = "환불 계좌 은행명", example = "국민은행")
        String refundAccountBank,

        @Schema(description = "환불 계좌 번호", example = "123-456-78901234")
        String refundAccountNumber,

        @Schema(description = "환불 계좌 예금주명", example = "홍길동")
        String refundAccountHolderName
) {
}

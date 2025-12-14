package com.nowayback.payment.presentation.payment.dto.response;

import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "결제 승인 응답 정보")
public record ConfirmPaymentResponse (
        @Schema(description = "결제 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID paymentId,

        @Schema(description = "사용자 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID userId,

        @Schema(description = "펀딩 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID fundingId,

        @Schema(description = "프로젝트 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID projectId,

        @Schema(description = "결제 금액", example = "10000")
        Long amount,

        @Schema(description = "결제 상태", example = "COMPLETED")
        PaymentStatus status,

        @Schema(description = "PG 결제 키", example = "pg_payment_key_123456")
        String pgPaymentKey,

        @Schema(description = "PG 주문 번호", example = "pg_order_id_123456")
        String pgOrderId,

        @Schema(description = "PG 결제 방식", example = "CARD")
        String pgMethod,

        @Schema(description = "환불 계좌 은행명", example = "국민은행")
        String refundAccountBank,

        @Schema(description = "환불 계좌 번호", example = "123-456-78901234")
        String refundAccountNumber,

        @Schema(description = "환불 계좌 예금주명", example = "홍길동")
        String refundAccountHolderName,

        @Schema(description = "결제 승인 일시", example = "2025-12-08T12:00:00")
        LocalDateTime approvedAt
) {

    public static ConfirmPaymentResponse from(PaymentResult result) {
        return new ConfirmPaymentResponse(
                result.paymentId(),
                result.userId(),
                result.fundingId(),
                result.projectId(),
                result.amount(),
                result.status(),
                result.pgPaymentKey(),
                result.pgOrderId(),
                result.pgMethod(),
                result.refundAccountBank() != null ? result.refundAccountBank() : null,
                result.refundAccountNumber() != null ? result.refundAccountNumber() : null,
                result.refundAccountHolderName() != null ? result.refundAccountHolderName() : null,
                result.approvedAt()
        );
    }
}

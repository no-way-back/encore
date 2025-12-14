package com.nowayback.payment.presentation.payment.dto.response;

import com.nowayback.payment.application.payment.dto.result.PaymentStatusLogResult;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "결제 상태 변경 로그 응답 정보")
public record PaymentStatusLogResponse (
        @Schema(description = "결제 상태 변경 로그 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID paymentStatusLogId,

        @Schema(description = "이전 결제 상태", example = "PENDING")
        PaymentStatus prevStatus,

        @Schema(description = "현재 결제 상태", example = "COMPLETED")
        PaymentStatus currStatus,

        @Schema(description = "변경 사유", example = "결제 승인 완료")
        String reason,

        @Schema(description = "결제 금액", example = "10000")
        long amount,

        @Schema(description = "로그 생성 일시", example = "2025-12-08T12:00:00")
        LocalDateTime createdAt
) {

    public static PaymentStatusLogResponse from(PaymentStatusLogResult result) {
        return new PaymentStatusLogResponse(
                result.paymentStatusLogId(),
                result.prevStatus(),
                result.currStatus(),
                result.reason(),
                result.amount(),
                result.createdAt()
        );
    }
}

package com.nowayback.payment.presentation.settlement.dto.response;

import com.nowayback.payment.application.settlement.dto.result.SettlementResult;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "정산 응답 정보")
public record SettlementResponse (

        @Schema(description = "정산 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID settlementId,

        @Schema(description = "프로젝트 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID projectId,

        @Schema(description = "총 정산 금액", example = "100000")
        Long totalAmount,

        @Schema(description = "서비스 수수료", example = "5000")
        Long serviceFee,

        @Schema(description = "PG 수수료", example = "3000")
        Long pgFee,

        @Schema(description = "순 정산 금액", example = "92000")
        Long netAmount,

        @Schema(description = "정산 상태", example = "COMPLETED")
        SettlementStatus status,

        @Schema(description = "정산 요청 일시", example = "2025-12-08T12:00:00")
        LocalDateTime requestedAt,

        @Schema(description = "정산 완료 일시", example = "2025-12-08T12:30:00")
        LocalDateTime completedAt,

        @Schema(description = "정산 생성 일시", example = "2025-12-08T12:00:00")
        LocalDateTime createdAt
) {

    public static SettlementResponse from(SettlementResult result) {
        return new SettlementResponse(
                result.settlementId(),
                result.projectId(),
                result.totalAmount(),
                result.serviceFee(),
                result.pgFee(),
                result.netAmount(),
                result.status(),
                result.requestedAt(),
                result.completedAt(),
                result.createdAt()
        );
    }
}

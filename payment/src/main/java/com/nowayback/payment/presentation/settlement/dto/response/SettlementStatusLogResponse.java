package com.nowayback.payment.presentation.settlement.dto.response;

import com.nowayback.payment.application.settlement.dto.result.SettlementStatusLogResult;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "정산 상태 로그 응답 정보")
public record SettlementStatusLogResponse (

        @Schema(description = "정산 상태 로그 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID settlementStatusLogId,

        @Schema(description = "이전 정산 상태", example = "PROCESSING")
        SettlementStatus prevStatus,

        @Schema(description = "현재 정산 상태", example = "COMPLETED")
        SettlementStatus currStatus,

        @Schema(description = "상태 변경 사유", example = "정산 완료")
        String reason,

        @Schema(description = "정산 금액", example = "92000")
        long amount,

        @Schema(description = "로그 생성 일시", example = "2025-12-08T12:00:00")
        LocalDateTime createdAt
) {

    public static SettlementStatusLogResponse from(SettlementStatusLogResult result) {
        return new SettlementStatusLogResponse(
                result.settlementStatusLogId(),
                result.prevStatus(),
                result.currStatus(),
                result.reason(),
                result.amount(),
                result.createdAt()
        );
    }
}

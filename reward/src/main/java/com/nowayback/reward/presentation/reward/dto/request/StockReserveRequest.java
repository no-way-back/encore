package com.nowayback.reward.presentation.reward.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

@Schema(description = "재고 예약 요청")
public record StockReserveRequest(
        @Schema(description = "펀딩 ID", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "fundingId는 필수입니다")
        UUID fundingId,

        @Schema(description = "재고 차감 항목 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "재고 차감 항목은 최소 1개 이상이어야 합니다")
        @Valid
        List<StockReserveItem> items
) {
    @Schema(description = "재고 예약 항목")
    public record StockReserveItem(
            @Schema(description = "리워드 ID", example = "123e4567-e89b-12d3-a456-426614174001", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull(message = "rewardId는 필수입니다")
            UUID rewardId,

            @Schema(description = "옵션 ID (선택사항)", example = "123e4567-e89b-12d3-a456-426614174002")
            UUID optionId,

            @Schema(description = "수량", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull(message = "수량은 필수입니다")
            @Positive(message = "수량은 1 이상이어야 합니다")
            Integer quantity
    ) {}
}
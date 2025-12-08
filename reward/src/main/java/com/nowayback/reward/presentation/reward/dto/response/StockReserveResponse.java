package com.nowayback.reward.presentation.reward.dto.response;

import com.nowayback.reward.application.reward.dto.StockReserveResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "재고 예약 응답")
public record StockReserveResponse(
        @Schema(description = "펀딩 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID fundingId,

        @Schema(description = "예약된 항목 목록")
        List<ReservedItem> reservedItems,

        @Schema(description = "총 금액", example = "105000")
        Long totalAmount
) {
    @Schema(description = "예약된 항목 정보")
    public record ReservedItem(
            @Schema(description = "예약 ID", example = "123e4567-e89b-12d3-a456-426614174001")
            UUID reservationId,

            @Schema(description = "리워드 ID", example = "123e4567-e89b-12d3-a456-426614174002")
            UUID rewardId,

            @Schema(description = "옵션 ID", example = "123e4567-e89b-12d3-a456-426614174003")
            UUID optionId,

            @Schema(description = "수량", example = "2")
            Integer quantity,

            @Schema(description = "항목 금액", example = "50000")
            Long itemAmount
    ) {}

    public static StockReserveResponse from(StockReserveResult result) {
        List<ReservedItem> items = result.reservedItems().stream()
                .map(r -> new ReservedItem(
                        r.reservationId(),
                        r.rewardId(),
                        r.optionId(),
                        r.quantity(),
                        r.itemAmount()
                ))
                .toList();

        return new StockReserveResponse(result.fundingId(), items, result.totalAmount());
    }
}
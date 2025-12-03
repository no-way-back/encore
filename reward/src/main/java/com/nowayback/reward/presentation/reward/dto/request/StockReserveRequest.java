package com.nowayback.reward.presentation.reward.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record StockReserveRequest(
        @NotNull(message = "fundingId는 필수입니다")
        UUID fundingId,

        @NotEmpty(message = "재고 차감 항목은 최소 1개 이상이어야 합니다")
        @Valid
        List<StockReserveItem> items
) {
    public record StockReserveItem(
            @NotNull(message = "rewardId는 필수입니다")
            UUID rewardId,

            UUID optionId,

            @NotNull(message = "수량은 필수입니다")
            @Positive(message = "수량은 1 이상이어야 합니다")
            Integer quantity
    ) {}
}
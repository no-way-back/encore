package com.nowayback.funding.presentation.funding.dto.request;

import java.util.List;
import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.presentation.funding.dto.request.validation.ValidFundingRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

@ValidFundingRequest
@Schema(description = "후원 생성 요청 DTO")
public record CreateFundingRequest(

    @Schema(description = "프로젝트 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @NotNull(message = "프로젝트 ID는 필수입니다.")
    UUID projectId,

    @Schema(description = "리워드 선택 목록")
    @Valid
    List<RewardItemRequest> rewardItems,

    @Schema(description = "기부금액", example = "5000")
    @Min(value = 1000, message = "후원 금액은 최소 1,000원 이상이어야 합니다.")
    Long donationAmount,

    @Schema(description = "PG 결제 키", example = "pg_test_key_123")
    @NotNull(message = "PG 결제 키는 필수입니다.")
    String pgPaymentKey,

    @Schema(description = "PG 주문 ID", example = "order_123")
    @NotNull(message = "PG 주문 ID는 필수입니다.")
    String pgOrderId,

    @Schema(description = "결제 수단", example = "CARD")
    @NotNull(message = "결제 수단은 필수입니다.")
    String pgMethod
) {

    public CreateFundingCommand toCommand(UUID userId, String idempotencyKey) {
        List<CreateFundingCommand.RewardItem> commandRewardItems = null;

        if (rewardItems != null && !rewardItems.isEmpty()) {
            commandRewardItems = rewardItems.stream()
                .map(item -> new CreateFundingCommand.RewardItem(
                    item.rewardId(),
                    item.optionId(),
                    item.quantity()
                ))
                .toList();
        }

        long safeDonationAmount = donationAmount != null ? donationAmount : 0L;

        return new CreateFundingCommand(
            projectId,
            userId,
            commandRewardItems,
            safeDonationAmount,
            pgPaymentKey,
            pgOrderId,
            pgMethod,
            idempotencyKey
        );
    }

    @Schema(description = "선택한 리워드 & 옵션 항목")
    public record RewardItemRequest(

        @Schema(description = "리워드 ID", example = "d2719b3c-01b1-4d32-9e86-9d6d9f540f2c")
        UUID rewardId,

        @Schema(description = "옵션 ID", example = "7ad1a23e-b5bb-4e92-afd6-cdbd1b42a5e1")
        UUID optionId,

        @Schema(description = "수량", example = "2")
        @Positive(message = "수량은 0보다 커야 합니다.")
        Integer quantity
    ) {}
}

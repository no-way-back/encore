package com.nowayback.project.presentation.projectdraft.dto.request;

import com.nowayback.project.application.projectdraft.command.SaveRewardDraftCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "리워드 드래프트 저장 요청")
public record SaveRewardDraftRequest(

    @Schema(description = "리워드 목록")
    List<RewardDraftRequest> rewards
) {

    public SaveRewardDraftCommand toCommand(UUID draftId, UUID userId) {

        List<SaveRewardDraftCommand.RewardDraftCommand> rewardCommands =
            rewards.stream()
                .map(reward ->
                    SaveRewardDraftCommand.RewardDraftCommand.of(
                        reward.title(),
                        reward.price(),
                        reward.limitCount(),
                        reward.shippingFee(),
                        reward.freeShippingAmount(),
                        reward.purchaseLimitPerPerson(),
                        reward.options() == null
                            ? List.of()
                            : reward.options().stream()
                                .map(o ->
                                    SaveRewardDraftCommand.RewardOptionCommand.of(
                                        o.name(),
                                        o.required(),
                                        o.additionalPrice(),
                                        o.stockQuantity(),
                                        o.displayOrder()
                                    )
                                )
                                .toList()
                    )
                )
                .toList();

        return SaveRewardDraftCommand.of(draftId, rewardCommands);
    }

    @Schema(description = "리워드 개별 정보")
    public record RewardDraftRequest(
        @Schema(description = "리워드 이름", example = "얼리버드 한정 패키지")
        String title,

        @Schema(description = "리워드 가격", example = "30000")
        Long price,

        @Schema(description = "총 제한 수량", example = "100")
        Integer limitCount,

        @Schema(description = "배송비", example = "3000")
        Integer shippingFee,

        @Schema(description = "무료배송 조건 금액", example = "50000")
        Integer freeShippingAmount,

        @Schema(description = "유저당 최대 구매 가능 수량", example = "2")
        Integer purchaseLimitPerPerson,

        @Schema(description = "리워드 옵션 목록")
        List<RewardOptionRequest> options
    ) {}

    @Schema(description = "리워드 옵션 정보")
    public record RewardOptionRequest(
        @Schema(description = "옵션명", example = "색상 선택")
        String name,

        @Schema(description = "필수 여부", example = "true")
        Boolean required,

        @Schema(description = "추가 금액", example = "2000")
        Integer additionalPrice,

        @Schema(description = "재고 수량", example = "50")
        Integer stockQuantity,

        @Schema(description = "표시 순서", example = "1")
        Integer displayOrder
    ) {}
}

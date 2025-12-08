package com.nowayback.reward.presentation.reward.dto.request;

import com.nowayback.reward.domain.reward.vo.RewardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Schema(description = "리워드 수정 요청")
public record UpdateRewardRequest(
        @Schema(description = "리워드 이름", example = "얼리버드 패키지")
        String name,

        @Schema(description = "리워드 설명", example = "프로젝트를 응원하는 얼리버드를 위한 특별 패키지")
        String description,

        @Schema(description = "가격", example = "50000")
        Long price,

        @Schema(description = "재고 수량", example = "100")
        Integer stockQuantity,

        @Schema(description = "배송비", example = "3000")
        Integer shippingFee,

        @Schema(description = "무료배송 기준 금액", example = "30000")
        Integer freeShippingAmount,

        @Schema(description = "1인당 구매 제한 수량", example = "5")
        Integer purchaseLimitPerPerson,

        @Schema(description = "리워드 타입", example = "PRODUCT")
        RewardType rewardType,

        @Schema(description = "옵션 목록")
        List<UpdateRewardOptionRequest> options
) {
    @Schema(description = "리워드 옵션 수정 요청")
    @Getter
    public static class UpdateRewardOptionRequest {
        @Schema(description = "옵션 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        private UUID optionId;

        @Schema(description = "옵션 이름", example = "블랙")
        private String name;

        @Schema(description = "추가 가격", example = "5000")
        private Long additionalPrice;

        @Schema(description = "재고 수량", example = "50")
        private Integer stockQuantity;

        @Schema(description = "필수 선택 여부", example = "true")
        private Boolean isRequired;

        @Schema(description = "표시 순서", example = "1")
        private Integer displayOrder;
    }
}
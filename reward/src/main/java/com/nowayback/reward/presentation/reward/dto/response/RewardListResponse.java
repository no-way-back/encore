package com.nowayback.reward.presentation.reward.dto.response;

import com.nowayback.reward.application.reward.dto.RewardListResult;
import com.nowayback.reward.domain.reward.vo.SaleStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "리워드 목록 응답")
public record RewardListResponse(
        @Schema(description = "리워드 목록")
        List<RewardResponse> rewards
) {
    public static RewardListResponse from(RewardListResult result) {
        List<RewardResponse> responses = result.rewards().stream()
                .map(RewardResponse::from)
                .toList();

        return new RewardListResponse(responses);
    }

    @Schema(description = "리워드 정보")
    public record RewardResponse(
            @Schema(description = "리워드 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            String rewardId,

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

            @Schema(description = "판매 상태", example = "AVAILABLE")
            SaleStatus status,

            @Schema(description = "옵션 목록")
            List<OptionResponse> optionList,

            @Schema(description = "생성 일시", example = "2024-12-01T10:00:00")
            LocalDateTime createdAt
    ) {
        public static RewardResponse from(RewardListResult.RewardItem item) {
            List<OptionResponse> options = item.optionList().stream()
                    .map(OptionResponse::from)
                    .toList();

            return new RewardResponse(
                    item.rewardId(),
                    item.name(),
                    item.description(),
                    item.price(),
                    item.stockQuantity(),
                    item.shippingFee(),
                    item.freeShippingAmount(),
                    item.purchaseLimitPerPerson(),
                    item.status(),
                    options,
                    item.createdAt()
            );
        }
    }

    @Schema(description = "리워드 옵션 정보")
    public record OptionResponse(
            @Schema(description = "옵션 ID", example = "123e4567-e89b-12d3-a456-426614174001")
            String optionId,

            @Schema(description = "옵션 이름", example = "블랙")
            String name,

            @Schema(description = "추가 가격", example = "5000")
            Long additionalPrice
    ) {
        public static OptionResponse from(RewardListResult.OptionItem item) {
            return new OptionResponse(
                    item.optionId(),
                    item.name(),
                    item.additionalPrice()
            );
        }
    }
}
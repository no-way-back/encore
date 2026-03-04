package com.nowayback.funding.domain.queue.vo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 대기 중인 펀딩 데이터
 *
 * 대기열 등록 시 함께 저장되는 펀딩 정보
 * Redis에 30분간 저장 후 자동 삭제
 */
public record PendingFunding(
        UUID userId,
        UUID projectId,
        Long amount,
        List<RewardItem> rewardItems,
        String idempotencyKey,
        LocalDateTime createdAt
) {

    /**
     * 리워드 아이템
     */
    public record RewardItem(
            UUID rewardId,
            UUID optionId,
            Integer quantity
    ) {
        public static RewardItem of(UUID rewardId, UUID optionId, Integer quantity) {
            return new RewardItem(rewardId, optionId, quantity);
        }
    }

    /**
     * 새로운 대기 펀딩 생성
     */
    public static PendingFunding create(
            UUID userId,
            UUID projectId,
            Long amount,
            List<RewardItem> rewardItems,
            String idempotencyKey
    ) {
        return new PendingFunding(
                userId,
                projectId,
                amount,
                rewardItems,
                idempotencyKey,
                LocalDateTime.now()
        );
    }

    /**
     * 리워드가 있는지 확인
     */
    public boolean hasRewards() {
        return rewardItems != null && !rewardItems.isEmpty();
    }
}
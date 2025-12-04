package com.nowayback.reward.domain.reward.vo;

import com.nowayback.reward.domain.exception.RewardException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.nowayback.reward.domain.exception.RewardErrorCode.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    private Integer quantity;
    private static final int MINIMUM_QUANTITY = 1;

    private Stock(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 재고 생성 (검증 포함)
     * - 최소 1개 이상이어야 함
     */
    public static Stock of(Integer quantity) {
        validateQuantity(quantity);
        return new Stock(quantity);
    }

    /**
     * 재고 생성 시 검증
     */
    private static void validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new RewardException(INVALID_STOCK_QUANTITY);
        }
        if (quantity < 0) {
            throw new RewardException(NEGATIVE_STOCK_QUANTITY);
        }
        if (quantity < MINIMUM_QUANTITY) {
            throw new RewardException(STOCK_BELOW_MINIMUM);
        }
    }

    /**
     * 재고 차감
     * - 차감 후 재고가 음수가 되면 예외 발생
     * - 재고가 0이 되는 것은 허용 (품절 상태)
     */
    public Stock decrease(Integer quantity) {
        int newQuantity = this.quantity - quantity;

        if (newQuantity < 0) {
            throw new RewardException(INSUFFICIENT_STOCK);
        }

        return new Stock(newQuantity);
    }

    /**
     * 재고 복원 (증가)
     * - 복원 후 재고가 원래 설정된 최대 재고를 초과하지 않아야 함
     * - 복원 수량은 양수여야 함
     */
    public Stock restore(Integer quantity) {
        validateRestoreQuantity(quantity);

        int newQuantity = this.quantity + quantity;

        return new Stock(newQuantity);
    }

    /**
     * 재고 복원 시 검증
     * - 복원 수량은 양수여야 함
     */
    private void validateRestoreQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new RewardException(INVALID_RESTORE_QUANTITY);
        }
    }

    /**
     * 재고 소진 여부 확인
     */
    public boolean isSoldOut() {
        return this.quantity == 0;
    }

    /**
     * 재고 보유 여부 확인
     */
    public boolean hasStock() {
        return this.quantity > 0;
    }
}
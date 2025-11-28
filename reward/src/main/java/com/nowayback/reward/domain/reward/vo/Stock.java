package com.nowayback.reward.domain.reward.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    private Integer quantity;

    private Stock(Integer quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    public static Stock of(Integer quantity) {
        return new Stock(quantity);
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("재고 수량을 입력해주세요.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException(
                    String.format("재고 수량은 0 이상이어야 합니다. (입력값: %d)", quantity)
            );
        }
    }

    public Stock decrease(Integer amount) {
        if (this.quantity < amount) {
            throw new IllegalStateException(
                    String.format("재고가 부족합니다. (현재 재고: %d, 요청 수량: %d)", this.quantity, amount)
            );
        }
        return new Stock(this.quantity - amount);
    }
}
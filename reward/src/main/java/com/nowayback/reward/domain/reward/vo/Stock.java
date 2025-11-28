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

    private Stock(Integer quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    public static Stock of(Integer quantity) {
        return new Stock(quantity);
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new RewardException(INVALID_STOCK_QUANTITY);
        }
        if (quantity < 0) {
            throw new RewardException(NEGATIVE_STOCK_QUANTITY);
        }
    }

    public Stock decrease(Integer amount) {
        if (this.quantity < amount) {
            throw new RewardException(INSUFFICIENT_STOCK);
        }
        return new Stock(this.quantity - amount);
    }
}
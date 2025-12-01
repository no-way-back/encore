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
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    public static Stock of(Integer quantity) {
        return new Stock(quantity);
    }

    /**
     * 품절 처리용 - 재고 0 생성
     * MINIMUM_QUANTITY 검증으로 0을 허용하지 않아
     * 품절 처리를 위한 별도 메서드 구현
     */
    public static Stock zero() {
        Stock stock = new Stock();
        stock.quantity = 0;
        return stock;
    }

    private void validateQuantity(Integer quantity) {
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

    public Stock decrease(Integer amount) {
        if (this.quantity < amount) {
            throw new RewardException(INSUFFICIENT_STOCK);
        }
        return new Stock(this.quantity - amount);
    }
}
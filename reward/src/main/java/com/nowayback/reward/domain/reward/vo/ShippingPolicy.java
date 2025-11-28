package com.nowayback.reward.domain.reward.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingPolicy {

    private Integer shippingFee;
    private Integer freeShippingAmount;

    private ShippingPolicy(Integer shippingFee, Integer freeShippingAmount) {
        validateShippingPolicy(shippingFee, freeShippingAmount);
        this.shippingFee = shippingFee;
        this.freeShippingAmount = freeShippingAmount;
    }

    public static ShippingPolicy of(Integer shippingFee, Integer freeShippingAmount) {
        return new ShippingPolicy(shippingFee, freeShippingAmount);
    }

    private void validateShippingPolicy(Integer shippingFee, Integer freeShippingAmount) {
        if (shippingFee == null) {
            throw new IllegalArgumentException("배송비를 입력해주세요.");
        }
        if (shippingFee < 0) {
            throw new IllegalArgumentException(
                    String.format("배송비는 0원 이상이어야 합니다. (입력값: %d원)", shippingFee)
            );
        }
        if (freeShippingAmount != null && freeShippingAmount < 0) {
            throw new IllegalArgumentException(
                    String.format("무료배송 기준금액은 0원 이상이어야 합니다. (입력값: %d원)", freeShippingAmount)
            );
        }
    }
}
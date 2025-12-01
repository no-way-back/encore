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
            throw new RewardException(INVALID_SHIPPING_FEE);
        }
        if (shippingFee < 0) {
            throw new RewardException(NEGATIVE_SHIPPING_FEE);
        }
        if (freeShippingAmount != null && freeShippingAmount < 0) {
            throw new RewardException(NEGATIVE_FREE_SHIPPING_AMOUNT);
        }
    }
}
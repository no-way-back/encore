package com.nowayback.project.domain.projectDraft.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RewardPrice {

    @Column(name = "price")
    private Long price;

    @Column(name = "shipping_fee")
    private Integer shippingFee;

    @Column(name = "free_shipping_amount")
    private Integer freeShippingAmount;

    public RewardPrice(Long price, Integer shippingFee, Integer freeShippingAmount) {
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Invalid price");
        }
        if (shippingFee != null && shippingFee < 0) {
            throw new IllegalArgumentException("Invalid shipping fee");
        }
        if (freeShippingAmount != null && freeShippingAmount < 0) {
            throw new IllegalArgumentException("Invalid free shipping amount");
        }

        this.price = price;
        this.shippingFee = shippingFee;
        this.freeShippingAmount = freeShippingAmount;
    }
}



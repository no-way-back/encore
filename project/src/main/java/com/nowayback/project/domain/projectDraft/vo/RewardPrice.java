package com.nowayback.project.domain.projectDraft.vo;

import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
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
        this.price = price;
        this.shippingFee = shippingFee;
        this.freeShippingAmount = freeShippingAmount;
    }

    public void validateForSubmission() {
        if (price != null || price <= 0) {
            throw new ProjectException(ProjectErrorCode.INVALID_REWARD_PRICE);
        }
    }
}



package com.nowayback.reward.domain.reward.vo;

import lombok.Getter;

@Getter
public enum SaleStatus {
    AVAILABLE("판매 가능"),
    SOLD_OUT("품절");

    private final String description;

    SaleStatus(String description) {
        this.description = description;
    }
}

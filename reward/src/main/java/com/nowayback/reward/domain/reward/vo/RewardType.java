package com.nowayback.reward.domain.reward.vo;

import lombok.Getter;

@Getter
public enum RewardType {
    GENERAL("상품"),
    TICKET("입장권");

    private final String description;

    RewardType(String description) {
        this.description = description;
    }
}

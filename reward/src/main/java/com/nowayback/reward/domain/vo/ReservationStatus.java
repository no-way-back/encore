package com.nowayback.reward.domain.vo;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    DEDUCTED("차감"),
    RESTORED("복원");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
}

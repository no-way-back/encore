package com.nowayback.reward.domain.stockreservation.vo;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("임시예약"),
    CONFIRMED("확정"),
    RESTORED("복원");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
}

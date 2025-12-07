package com.nowayback.reward.domain.qrcode.vo;

import lombok.Getter;

@Getter
public enum QrCodeStatus {
    UNUSED("미사용"),
    USED("사용 완료"),
    CANCELLED("취소");

    private final String description;

    QrCodeStatus(String description) {
        this.description = description;
    }
}

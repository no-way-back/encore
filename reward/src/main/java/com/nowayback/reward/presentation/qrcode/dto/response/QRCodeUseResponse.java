package com.nowayback.reward.presentation.qrcode.dto.response;

import com.nowayback.reward.application.qrcode.dto.QRCodeUseResult;
import com.nowayback.reward.domain.qrcode.vo.QrCodeStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record QRCodeUseResponse(
        UUID qrCodeId,
        UUID fundingId,
        QrCodeStatus status,
        LocalDateTime usedAt
) {
    public static QRCodeUseResponse from(QRCodeUseResult result) {
        return new QRCodeUseResponse(
                result.qrCodeId(),
                result.fundingId(),
                result.status(),
                result.usedAt()
        );
    }
}
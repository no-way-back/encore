package com.nowayback.reward.application.qrcode.dto;

import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import com.nowayback.reward.domain.qrcode.vo.QrCodeStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record QRCodeUseResult(
        UUID qrCodeId,
        UUID fundingId,
        QrCodeStatus status,
        LocalDateTime usedAt
) {
    public static QRCodeUseResult of(QRCodes qrCode) {
        return new QRCodeUseResult(
                qrCode.getId(),
                qrCode.getFundingId().getId(),
                qrCode.getStatus(),
                qrCode.getUsedAt()
        );
    }
}
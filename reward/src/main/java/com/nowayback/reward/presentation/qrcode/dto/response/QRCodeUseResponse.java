package com.nowayback.reward.presentation.qrcode.dto.response;

import com.nowayback.reward.application.qrcode.dto.QRCodeUseResult;
import com.nowayback.reward.domain.qrcode.vo.QrCodeStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "QR 코드 사용 응답")
public record QRCodeUseResponse(
        @Schema(description = "QR 코드 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID qrCodeId,

        @Schema(description = "펀딩 ID", example = "123e4567-e89b-12d3-a456-426614174001")
        UUID fundingId,

        @Schema(description = "QR 코드 상태", example = "USED")
        QrCodeStatus status,

        @Schema(description = "사용 일시", example = "2024-12-08T10:30:00")
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
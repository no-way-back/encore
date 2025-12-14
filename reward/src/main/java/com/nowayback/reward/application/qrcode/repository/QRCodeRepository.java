package com.nowayback.reward.application.qrcode.repository;

import com.nowayback.reward.domain.qrcode.entity.QRCodes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QRCodeRepository {
    Optional<QRCodes> findById(UUID qrCodeId);
    QRCodes save(QRCodes qrCode);
    List<QRCodes> saveAll(List<QRCodes> qrCodes);

    List<QRCodes> findByFundingId(UUID fundingId);
}

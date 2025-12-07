package com.nowayback.reward.domain.qrcode.repository;

import com.nowayback.reward.domain.qrcode.entity.QRCodes;

import java.util.List;

public interface QRCodeRepository {
    QRCodes save(QRCodes qrCode);
    List<QRCodes> saveAll(List<QRCodes> qrCodes);
}

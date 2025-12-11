package com.nowayback.reward.application.port;

import com.nowayback.reward.domain.qrcode.entity.QRCodes;

import java.util.UUID;

public interface QRCodeCreator {
    QRCodes createCode(UUID rewardId, UUID fundingId, String email, String title);
}
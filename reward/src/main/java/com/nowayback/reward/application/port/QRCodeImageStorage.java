package com.nowayback.reward.application.port;

import java.util.UUID;

public interface QRCodeImageStorage {
    String saveQRCodeImage(UUID qrCodeId, byte[] imageData);
}
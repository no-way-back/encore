package com.nowayback.reward.infrastructure.qrcode;

import com.nowayback.reward.application.port.QRCodeCreator;
import com.nowayback.reward.application.port.QRCodeImageStorage;
import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class QRCodeCreatorImpl implements QRCodeCreator {

    private final QRCodeGenerator qrCodeGenerator;
    private final QRCodeImageStorage imageStorage;

    @Override
    public QRCodes createCode(UUID rewardId, UUID fundingId, String email, String title) {
        UUID id = UUID.randomUUID();

        byte[] image = qrCodeGenerator.generateQRCodeImage(id);
        String imageUrl = imageStorage.saveQRCodeImage(id, image);

        log.debug("QR 코드 생성 완료 - ID: {}", id);

        return QRCodes.createWithId(id, rewardId, fundingId, email, title, imageUrl);
    }
}
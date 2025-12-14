package com.nowayback.reward.infrastructure.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class QRCodeGenerator {

    @Value("${qrcode.width}")
    private int width;

    @Value("${qrcode.height}")
    private int height;

    @Value("${qrcode.base-url}")
    private String baseUrl;

    /**
     * QR 코드 이미지를 byte 배열로 생성
     * @param qrCodeId QR 코드 ID
     * @return QR 코드 이미지 byte 배열
     */
    public byte[] generateQRCodeImage(UUID qrCodeId) {
        try {
            String qrContent = generateQRContent(qrCodeId);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    qrContent,
                    BarcodeFormat.QR_CODE,
                    width,
                    height
            );

            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);

            log.info("QR 코드 이미지 생성 완료 - ID: {}", qrCodeId);
            return baos.toByteArray();

        } catch (WriterException | IOException e) {
            log.error("QR 코드 이미지 생성 실패 - ID: {}", qrCodeId, e);
            throw new RuntimeException("QR 코드 이미지 생성에 실패했습니다", e);
        }
    }

    /**
     * QR 코드에 담을 내용 생성 (검증 API URL)
     */
    private String generateQRContent(UUID qrCodeId) {
        return String.format("%s/qrcodes/%s/use", baseUrl, qrCodeId);
    }
}
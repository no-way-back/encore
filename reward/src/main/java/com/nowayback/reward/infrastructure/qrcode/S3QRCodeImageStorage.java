package com.nowayback.reward.infrastructure.qrcode;

import com.nowayback.reward.application.port.QRCodeImageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3QRCodeImageStorage implements QRCodeImageStorage {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.s3.base-url}")
    private String s3BaseUrl;

    private final S3Client s3Client;

    @Override
    public String saveQRCodeImage(UUID qrCodeId, byte[] imageData) {
        String fileName = String.format("qrcodes/%s.png", qrCodeId);

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType("image/png")
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(imageData));

            String imageUrl = s3BaseUrl + "/" + fileName;
            log.info("S3 업로드 성공: {}", imageUrl);

            return imageUrl;

        } catch (SdkException e) {
            log.error("S3 업로드 실패 - QR Code ID: {}", qrCodeId, e);
            throw new RuntimeException("QR 코드 이미지 S3 업로드 실패", e);
        }
    }
}
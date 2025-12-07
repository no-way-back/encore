package com.nowayback.reward.presentation.qrcode;

import com.nowayback.reward.application.qrcode.QRCodeService;
import com.nowayback.reward.application.qrcode.dto.QRCodeUseResult;
import com.nowayback.reward.presentation.qrcode.dto.response.QRCodeUseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/qrcodes")
@RequiredArgsConstructor
public class QRCodeController {

    private final QRCodeService qrCodeService;

    /**
     * QR 코드 사용 처리
     */
    @PostMapping("/{qrCodeId}/use")
    public ResponseEntity<QRCodeUseResponse> useQRCode(@PathVariable UUID qrCodeId) {
        QRCodeUseResult result = qrCodeService.useQRCode(qrCodeId);

        return ResponseEntity.ok(
                QRCodeUseResponse.from(result)
        );
    }
}
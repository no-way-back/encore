package com.nowayback.reward.presentation.qrcode;

import com.nowayback.reward.application.qrcode.QRCodeService;
import com.nowayback.reward.application.qrcode.dto.QRCodeUseResult;
import com.nowayback.reward.presentation.qrcode.dto.response.QRCodeUseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/qrcodes")
@RequiredArgsConstructor
public class QRCodeController implements QRCodeControllerDoc {

    private final QRCodeService qrCodeService;

    @Override
    @PostMapping("/{qrCodeId}/use")
    public ResponseEntity<QRCodeUseResponse> useQRCode(@PathVariable UUID qrCodeId) {
        QRCodeUseResult result = qrCodeService.useQRCode(qrCodeId);

        return ResponseEntity.ok(
                QRCodeUseResponse.from(result)
        );
    }
}
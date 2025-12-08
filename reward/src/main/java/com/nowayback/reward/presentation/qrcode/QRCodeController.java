package com.nowayback.reward.presentation.qrcode;

import com.nowayback.reward.application.qrcode.QRCodeService;
import com.nowayback.reward.application.qrcode.dto.QRCodeUseResult;
import com.nowayback.reward.presentation.qrcode.dto.response.QRCodeUseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "QR Code", description = "QR 코드 관리 API")
@RestController
@RequestMapping("/qrcodes")
@RequiredArgsConstructor
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @Operation(summary = "QR 코드 사용", description = "QR 코드를 스캔하여 사용 처리합니다.")
    @PostMapping("/{qrCodeId}/use")
    public ResponseEntity<QRCodeUseResponse> useQRCode(
            @Parameter(description = "QR 코드 ID", required = true)
            @PathVariable UUID qrCodeId
    ) {
        QRCodeUseResult result = qrCodeService.useQRCode(qrCodeId);

        return ResponseEntity.ok(
                QRCodeUseResponse.from(result)
        );
    }
}
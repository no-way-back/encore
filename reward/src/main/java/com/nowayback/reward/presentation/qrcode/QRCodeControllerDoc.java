package com.nowayback.reward.presentation.qrcode;

import com.nowayback.reward.presentation.qrcode.dto.response.QRCodeUseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "QR Code API", description = "QR 코드 관리 API")
public interface QRCodeControllerDoc {

    @Operation(
            summary = "QR 코드 사용",
            description = "QR 코드를 스캔하여 사용 처리합니다. 이미 사용된 QR 코드는 다시 사용할 수 없습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "QR 코드 사용 성공",
                    content = @Content(schema = @Schema(implementation = QRCodeUseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "QR 코드를 찾을 수 없음 (QRCODE_NOT_FOUND)"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 사용된 QR 코드 또는 사용 불가능한 상태"
            )
    })
    ResponseEntity<QRCodeUseResponse> useQRCode(
            @Parameter(description = "QR 코드 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID qrCodeId
    );
}
package com.nowayback.payment.presentation.payment;

import com.nowayback.payment.presentation.exception.response.ErrorResponse;
import com.nowayback.payment.presentation.exception.response.FieldErrorResponse;
import com.nowayback.payment.presentation.payment.dto.request.ConfirmPaymentRequest;
import com.nowayback.payment.presentation.payment.dto.request.RefundPaymentRequest;
import com.nowayback.payment.presentation.payment.dto.response.ConfirmPaymentResponse;
import com.nowayback.payment.presentation.payment.dto.response.RefundPaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Internal Payment API", description = "내부 결제 API")
public interface InternalPaymentControllerDoc {

    @Operation(summary = "결제 승인", description = "결제 승인을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "결제 승인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FieldErrorResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<ConfirmPaymentResponse> confirmPayment(
            @Parameter(description = "사용자 ID", required = true)
            @RequestHeader(name = "X-User-Id") java.util.UUID userId,
            @Parameter(description = "결제 승인 요청 정보", required = true)
            @RequestBody ConfirmPaymentRequest request
    );

    @Operation(summary = "결제 환불", description = "결제 환불을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 환불 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FieldErrorResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<RefundPaymentResponse> refundPayment(
            @Parameter(description = "결제 환불 요청 정보", required = true)
            @RequestBody RefundPaymentRequest request
    );
}
package com.nowayback.payment.presentation.payment;

import com.nowayback.payment.presentation.dto.response.PageResponse;
import com.nowayback.payment.presentation.exception.response.ErrorResponse;
import com.nowayback.payment.presentation.payment.dto.response.PaymentStatusLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Tag(name = "Payment Status Log API", description = "결제 상태 로그 조회 API")
public interface PaymentStatusLogControllerDoc {

    @Operation(summary = "결제 상태 로그 조회", description = "결제 ID 조건으로 상태 로그를 조회합니다. (MASTER/ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 상태 로그 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<PageResponse<PaymentStatusLogResponse>> getPaymentStatusLogs(
            @Parameter(description = "조회할 결제 ID", required = false) @RequestParam(required = false) UUID paymentId,
            @Parameter(description = "페이지 번호 (0부터 시작)", required = false) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", required = false) @RequestParam(required = false, defaultValue = "10") int size
    );
}
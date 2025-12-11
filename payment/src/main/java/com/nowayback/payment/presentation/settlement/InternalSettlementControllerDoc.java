package com.nowayback.payment.presentation.settlement;

import com.nowayback.payment.presentation.exception.response.FieldErrorResponse;
import com.nowayback.payment.presentation.settlement.dto.response.SettlementResponse;
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

@Tag(name = "Internal Settlement API", description = "내부 정산 API")
public interface InternalSettlementControllerDoc {

    @Operation(summary = "정산 처리", description = "프로젝트에 대한 정산을 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "정산 처리 성공"),
            @ApiResponse(responseCode = "409", description = "정산 처리 중복 요청",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FieldErrorResponse.class))
            )
    })
    ResponseEntity<SettlementResponse> processSettlement(
            @Parameter(description = "정산할 프로젝트 ID", required = true)
            @PathVariable("projectId") UUID projectId
    );
}
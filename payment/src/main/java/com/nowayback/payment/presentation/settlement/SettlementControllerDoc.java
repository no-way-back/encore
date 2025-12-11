package com.nowayback.payment.presentation.settlement;

import com.nowayback.payment.presentation.exception.response.ErrorResponse;
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

@Tag(name = "Settlement API", description = "정산 API")
public interface SettlementControllerDoc {

    @Operation(summary = "정산 조회", description = "프로젝트의 정산 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정산 조회 성공"),
            @ApiResponse(responseCode = "404", description = "정산 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<SettlementResponse> getSettlement(
            @Parameter(description = "조회할 프로젝트 ID", required = true)
            @PathVariable("projectId") UUID projectId
    );
}
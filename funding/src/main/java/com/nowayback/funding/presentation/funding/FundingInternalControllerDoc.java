package com.nowayback.funding.presentation.funding;

import com.nowayback.funding.presentation.exception.FundingExceptionHandler.ErrorResponse;
import com.nowayback.funding.presentation.funding.dto.response.FundingDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Funding Internal API", description = "펀딩 서비스 내부용 API")
public interface FundingInternalControllerDoc {

    @Operation(
        summary = "펀딩 단건 상세 조회 (내부용)",
        description = "fundingId를 기준으로 펀딩 상세 정보를 조회합니다. 내부 마이크로서비스 간 통신 용도입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "펀딩 내역 없음 (FD-APP-008)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<FundingDetailResponse> getFundingDetail(
        @PathVariable UUID fundingId
    );
}

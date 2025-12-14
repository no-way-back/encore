package com.nowayback.funding.presentation.fundingProjectStatistics;

import com.nowayback.funding.presentation.exception.FundingExceptionHandler.ErrorResponse;
import com.nowayback.funding.presentation.fundingProjectStatistics.dto.response.FundingProjectStatisticsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Funding Project Statistics API", description = "펀딩 프로젝트 통계 API")
public interface FundingProjectStatisticsControllerDoc {

    @Operation(
        summary = "프로젝트 펀딩 통계 조회",
        description = "특정 프로젝트의 펀딩 현황 및 통계를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "프로젝트 또는 통계 정보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<FundingProjectStatisticsResponse> getFundingProjectStatistics(
        @PathVariable("projectId") UUID projectId
    );
}

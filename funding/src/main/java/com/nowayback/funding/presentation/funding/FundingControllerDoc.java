package com.nowayback.funding.presentation.funding;

import com.nowayback.funding.presentation.exception.FundingExceptionHandler.ErrorResponse;
import com.nowayback.funding.presentation.funding.dto.request.*;
import com.nowayback.funding.presentation.funding.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Funding API", description = "펀딩 서비스 API")
public interface FundingControllerDoc {

    @Operation(
        summary = "후원 생성",
        description = "리워드 선택 또는 기부금 입력을 통해 후원을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "후원 생성 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "유효하지 않은 요청 데이터 (FD-APP-007 등)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "중복 요청 (FD-APP-001)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CreateFundingResponse> createFunding(
        @RequestHeader("X-User-Id") UUID userId,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
        @RequestBody CreateFundingRequest request
    );

    @Operation(
        summary = "후원 취소",
        description = "특정 후원을 취소합니다. 취소 사유는 필수입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "취소 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "후원 내역 없음 (FD-APP-008)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "본인만 취소 가능 (FD-APP-009)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CancelFundingResponse> cancelFunding(
        @PathVariable UUID fundingId,
        @RequestHeader("X-User-Id") UUID userId,
        @RequestBody CancelFundingRequest request
    );

    @Operation(
        summary = "내 후원 내역 조회",
        description = "로그인한 유저의 후원 내역을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<GetMyFundingsResponse> getMyFundings(
        @RequestHeader("X-User-Id") UUID userId,
        @ModelAttribute GetMyFundingsRequest request
    );

    @Operation(
        summary = "프로젝트 후원자 목록 조회",
        description = "프로젝트 생성자만 후원자 목록을 조회할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "프로젝트 생성자가 아님 (FD-APP-011)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<GetProjectSponsorsResponse> getProjectSponsors(
        @PathVariable UUID projectId,
        @RequestHeader("X-User-Id") UUID creatorId,
        @ModelAttribute GetProjectSponsorsRequest request
    );
}

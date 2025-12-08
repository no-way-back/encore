package com.nowayback.reward.presentation.reward;

import com.nowayback.reward.presentation.reward.dto.request.StockReserveRequest;
import com.nowayback.reward.presentation.reward.dto.response.RewardListResponse;
import com.nowayback.reward.presentation.reward.dto.response.StockReserveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@Tag(name = "Internal Reward API", description = "내부 마이크로서비스 간 통신용 리워드 API")
public interface InternalRewardControllerDoc {

    @Operation(
            summary = "프로젝트의 리워드 목록 조회",
            description = "특정 프로젝트에 속한 판매 가능한 모든 리워드를 조회합니다. (내부 서비스 전용)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공 (리워드가 없어도 빈 리스트 반환)",
                    content = @Content(schema = @Schema(implementation = RewardListResponse.class))
            )
    })
    ResponseEntity<RewardListResponse> getById(
            @Parameter(description = "프로젝트 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID projectId
    );

    @Operation(
            summary = "리워드 재고 예약",
            description = """
                    펀딩 시 리워드 재고를 예약합니다.
                    - 옵션이 있는 경우: 옵션의 재고 차감
                    - 옵션이 없는 경우: 리워드의 재고 차감
                    - 필수 옵션이 있는데 선택하지 않은 경우: 에러
                    - StockReservation 생성 (DEDUCTED 상태)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "재고 예약 성공",
                    content = @Content(schema = @Schema(implementation = StockReserveResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            잘못된 요청
                            - 유효성 검증 실패
                            - INVALID_STOCK_QUANTITY: 재고 수량이 유효하지 않음
                            - NEGATIVE_STOCK_QUANTITY: 재고가 음수
                            - STOCK_BELOW_MINIMUM: 재고가 최소값 미만
                            - INVALID_RESTORE_QUANTITY: 복원 수량이 유효하지 않음
                            """
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "REWARD_NOT_FOUND: 리워드를 찾을 수 없음"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                            재고 부족 또는 비즈니스 규칙 위반
                            - INSUFFICIENT_STOCK: 재고 부족
                            - 필수 옵션 미선택
                            """
            )
    })
    ResponseEntity<StockReserveResponse> reserveStock(
            @Parameter(description = "사용자 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestHeader(value = "X-User-Id") UUID userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "재고 예약 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StockReserveRequest.class))
            )
            @Valid @RequestBody StockReserveRequest request
    );
}
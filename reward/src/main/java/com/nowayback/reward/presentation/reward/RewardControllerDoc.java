package com.nowayback.reward.presentation.reward;

import com.nowayback.reward.presentation.reward.dto.request.UpdateRewardRequest;
import com.nowayback.reward.presentation.reward.dto.response.RewardListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "Reward API", description = "리워드 관리 API")
public interface RewardControllerDoc {

    @Operation(
            summary = "프로젝트의 리워드 목록 조회",
            description = "특정 프로젝트에 속한 판매 가능한 모든 리워드를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공 (리워드가 없어도 빈 리스트 반환)",
                    content = @Content(schema = @Schema(implementation = RewardListResponse.class))
            )
    })
    ResponseEntity<?> getById(
            @Parameter(description = "프로젝트 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("projectId") UUID projectId
    );

    @Operation(
            summary = "리워드 정보 수정",
            description = """
                    리워드의 정보를 수정합니다.
                    - null 값은 기존 값 유지
                    - 옵션 수정은 Cascade로 변경감지 활용
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            잘못된 요청
                            - PRICE_BELOW_MINIMUM: 가격이 최소값 미만
                            - INVALID_STOCK_QUANTITY: 재고 수량이 유효하지 않음
                            - NEGATIVE_STOCK_QUANTITY: 재고가 음수
                            - STOCK_BELOW_MINIMUM: 재고가 최소값 미만
                            """
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "REWARD_NOT_FOUND: 리워드를 찾을 수 없음"
            )
    })
    ResponseEntity<UpdateRewardRequest> updateReward(
            @Parameter(description = "리워드 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID rewardId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "리워드 수정 정보 (null인 필드는 기존값 유지)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateRewardRequest.class))
            )
            @RequestBody UpdateRewardRequest request
    );

    @Operation(
            summary = "리워드 삭제",
            description = "리워드를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "REWARD_NOT_FOUND: 리워드를 찾을 수 없음"
            )
    })
    ResponseEntity<Void> deleteReward(
            @Parameter(description = "리워드 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID rewardId
    );
}
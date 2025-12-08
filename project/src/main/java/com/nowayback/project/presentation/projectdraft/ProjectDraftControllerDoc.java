package com.nowayback.project.presentation.projectdraft;

import com.nowayback.project.presentation.exception.ProjectExceptionHandler.ErrorResponse;
import com.nowayback.project.presentation.projectdraft.dto.request.*;
import com.nowayback.project.presentation.projectdraft.dto.response.*;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Project Draft API", description = "프로젝트 드래프트 관리 API")
public interface ProjectDraftControllerDoc {

    @Operation(
        summary = "프로젝트 드래프트 생성",
        description = "유저가 프로젝트 생성을 시작할 때 새로운 드래프트를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "드래프트 생성 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "User ID 누락 (PROJECT1001)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                {
                  "code": "PROJECT1001",
                  "message": "User ID는 필수입니다.",
                  "status": 400
                }
                """)
            )
        )
    })
    ResponseEntity<ProjectDraftCreateResponse> createProjectDraft(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId
    );


    @Operation(summary = "스토리 드래프트 저장", description = "스토리 정보를 드래프트에 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "스토리 저장 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "스토리 정보 유효성 오류 (PROJECT1028~1033)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                {
                  "code": "PROJECT1028",
                  "message": "제목은 공백일 수 없습니다.",
                  "status": 400
                }
                """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "스토리 드래프트 없음 (PROJECT2003)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                {
                  "code": "PROJECT2003",
                  "message": "스토리 드래프트를 찾을 수 없습니다.",
                  "status": 404
                }
                """)
            )
        )
    })
    ResponseEntity<ProjectStoryDraftResponse> saveProjectStoryDraft(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
        @PathVariable UUID draftId,
        @RequestBody SaveProjectStoryDraftRequest request
    );


    @Operation(summary = "펀딩 드래프트 저장", description = "펀딩 정보를 드래프트에 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "펀딩 저장 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "펀딩 정보 유효성 오류 (PROJECT1006~1015)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                {
                  "code": "PROJECT1006",
                  "message": "목표 금액은 0보다 커야 합니다.",
                  "status": 400
                }
                """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "펀딩 드래프트 없음 (PROJECT2004)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                {
                  "code": "PROJECT2004",
                  "message": "펀딩 드래프트를 찾을 수 없습니다.",
                  "status": 404
                }
                """)
            )
        )
    })
    ResponseEntity<ProjectFundingDraftResponse> saveProjectFundingDraft(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
        @PathVariable UUID draftId,
        @RequestBody SaveProjectFundingDraftRequest request
    );


    @Operation(summary = "정산 드래프트 저장", description = "정산 정보를 드래프트에 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정산 저장 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "정산 정보 유효성 오류 (PROJECT1024~1027)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                {
                  "code": "PROJECT1024",
                  "message": "은행명은 공백일 수 없습니다.",
                  "status": 400
                }
                """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "정산 드래프트 없음 (PROJECT2005)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                {
                  "code": "PROJECT2005",
                  "message": "정산 드래프트를 찾을 수 없습니다.",
                  "status": 404
                }
                """)
            )
        )
    })
    ResponseEntity<ProjectSettlementDraftResponse> saveProjectSettlementDraft(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
        @PathVariable UUID draftId,
        @RequestBody SaveProjectSettlementDraft request
    );


    @Operation(summary = "리워드 드래프트 저장", description = "리워드 및 옵션 정보를 드래프트에 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "리워드 저장 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "리워드 정보 유효성 오류 (PROJECT1016~1023)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                {
                  "code": "PROJECT1016",
                  "message": "리워드 가격은 0보다 커야 합니다.",
                  "status": 400
                }
                """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "리워드 드래프트 없음 (PROJECT2006)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                {
                  "code": "PROJECT2006",
                  "message": "리워드 드래프트를 찾을 수 없습니다.",
                  "status": 404
                }
                """)
            )
        )
    })
    ResponseEntity<ProjectRewardDraftResponse> saveRewardDraft(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
        @PathVariable UUID projectDraftId,
        @RequestBody SaveRewardDraftRequest request
    );


    @Operation(summary = "펀딩 드래프트 조회", description = "Draft ID 기준으로 저장된 펀딩 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "펀딩 드래프트 없음 (PROJECT2004)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<ProjectFundingDraftResponse> getFundingDraft(@PathVariable UUID projectDraftId);


    @Operation(summary = "리워드 드래프트 조회", description = "Draft ID 기준으로 저장된 리워드 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "리워드 드래프트 없음 (PROJECT2006)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<ProjectRewardDraftResponse> getRewardDraft(@PathVariable UUID projectDraftId);


    @Operation(summary = "스토리 드래프트 조회", description = "Draft ID 기준으로 저장된 스토리 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "스토리 드래프트 없음 (PROJECT2003)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<ProjectStoryDraftResponse> getStoryDraft(@PathVariable UUID projectDraftId);


    @Operation(summary = "정산 드래프트 조회", description = "Draft ID 기준으로 저장된 정산 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "정산 드래프트 없음 (PROJECT2005)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<ProjectSettlementDraftResponse> getSettlementDraft(@PathVariable UUID projectDraftId);


    @Operation(summary = "내 드래프트 목록 조회", description = "현재 로그인한 유저의 프로젝트 드래프트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<PageResponse<ProjectDraftResponse>> getDrafts(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
        @RequestParam(required = false) ProjectDraftStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    );


    @Operation(summary = "프로젝트 드래프트 제출", description = "스토리/펀딩/리워드/정산이 모두 작성된 경우 프로젝트를 제출합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "제출 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "모든 단계가 완료되지 않음 (PROJECT1012)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 제출된 상태 등 제출 불가 (PROJECT2002)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "프로젝트 드래프트 없음 (PROJECT2001)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> submitDraft(@PathVariable UUID projectDraftId);
}

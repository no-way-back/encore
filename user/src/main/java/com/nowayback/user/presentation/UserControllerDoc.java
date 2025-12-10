package com.nowayback.user.presentation;

import com.nowayback.user.infrastructure.auth.user.AuthUser;
import com.nowayback.user.infrastructure.auth.user.CurrentUser;
import com.nowayback.user.presentation.dto.request.SearchUserRequest;
import com.nowayback.user.presentation.dto.request.UpdateUserEmailRequest;
import com.nowayback.user.presentation.dto.request.UpdateUserInfoRequest;
import com.nowayback.user.presentation.dto.response.PageResponse;
import com.nowayback.user.presentation.dto.response.UserResponse;
import com.nowayback.user.presentation.exception.response.ErrorResponse;
import com.nowayback.user.presentation.exception.response.FieldErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "User API", description = "유저 API")
public interface UserControllerDoc {

    @Operation(summary = "회원 단건 조회", description = "userId에 해당하는 회원 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<UserResponse> getUser(
            @Parameter(hidden = true) @CurrentUser AuthUser authUser,
            @Parameter(description = "검색할 사용자 ID", required = true)
            @PathVariable("userId") UUID userId
    );

    @Operation(summary = "회원 목록 검색", description = "키워드, 역할, 상태 등 조건으로 회원 목록을 페이지네이션하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<PageResponse<UserResponse>> getUsers(
            @RequestBody SearchUserRequest request,
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "이메일 변경", description = "본인의 이메일을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FieldErrorResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<UserResponse> updateEmail(
            @Parameter(hidden = true) @CurrentUser AuthUser authUser,
            @Valid @RequestBody UpdateUserEmailRequest request
    );

    @Operation(summary = "회원 정보 변경", description = "관리자 또는 본인이 닉네임 등을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FieldErrorResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<UserResponse> updateInfo(
            @Parameter(hidden = true) @CurrentUser AuthUser authUser,
            @Valid @RequestBody UpdateUserInfoRequest request
    );

    @Operation(summary = "관리자 승인", description = "MASTER 권한이 관리자 가입 승인을 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자 승인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<UserResponse> approveAdmin(
            @Parameter(description = "승인할 사용자 ID", required = true)
            @PathVariable("userId") UUID userId
    );

    @Operation(summary = "회원 비활성화", description = "회원 탈퇴 또는 비활성화 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 비활성화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> deactivateUser(
            @Parameter(hidden = true) @CurrentUser AuthUser authUser,
            @Parameter(description = "비활성화할 사용자 ID", required = true)
            @PathVariable("userId") UUID userId
    );
}
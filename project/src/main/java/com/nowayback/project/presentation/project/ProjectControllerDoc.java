package com.nowayback.project.presentation.project;

import com.nowayback.project.presentation.exception.ProjectExceptionHandler.ErrorResponse;
import com.nowayback.project.presentation.project.dto.response.ProjectResponse;
import com.nowayback.project.presentation.project.dto.response.SettlementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Project API", description = "프로젝트 API")
public interface ProjectControllerDoc {

    @Operation(summary = "프로젝트 단건 조회", description = "프로젝트 ID로 프로젝트 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "프로젝트를 찾을 수 없음 (PROJECT2008)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                    {
                      "code": "PROJECT2008",
                      "message": "프로젝트를 찾을 수 없습니다.",
                      "status": 404
                    }
                """)
            )
        )
    })
    ResponseEntity<ProjectResponse> getProject(@PathVariable UUID projectId);

    @Operation(summary = "프로젝트 정산 정보 조회", description = "프로젝트 ID로 정산 계좌 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "정산 정보 또는 프로젝트 없음 (PROJECT2005, PROJECT2008)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject("""
                    {
                      "code": "PROJECT2005",
                      "message": "정산 정보를 찾을 수 없습니다.",
                      "status": 404
                    }
                """)
            )
        )
    })
    ResponseEntity<SettlementResponse> getProjectAccount(@PathVariable UUID projectId);
}

package com.nowayback.funding.presentation.funding.dto.request;

import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.GetProjectSponsorsCommand;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 후원자 목록 조회 요청 DTO")
public record GetProjectSponsorsRequest(

    @Schema(description = "페이지 번호", example = "0")
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    Integer page,

    @Schema(description = "페이지 크기", example = "20")
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
    Integer size
) {

    public GetProjectSponsorsCommand toCommand(UUID projectId, UUID creatorId) {
        return new GetProjectSponsorsCommand(
            projectId,
            creatorId,
            page != null ? page : 0,
            size != null ? size : 20
        );
    }
}

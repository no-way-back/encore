package com.nowayback.project.presentation.projectdraft.dto.request;

import com.nowayback.project.application.projectdraft.command.SaveStoryDraftCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "스토리 드래프트 저장 요청")
public record SaveProjectStoryDraftRequest(

    @Schema(description = "스토리 제목", example = "나만의 음악 프로젝트를 소개합니다")
    String title,

    @Schema(description = "스토리 요약", example = "이 프로젝트는 새로운 앨범 제작을 위한 펀딩 프로젝트입니다.")
    String summary,

    @Schema(description = "카테고리", example = "MUSIC")
    String category,

    @Schema(description = "썸네일 이미지 URL", example = "https://thumbnail.png")
    String thumbnailUrl,

    @Schema(description = "본문 JSON 문자열", example = "JSON")
    String contentJson
) {

    public SaveStoryDraftCommand toCommand(UUID draftId, UUID userId) {
        return SaveStoryDraftCommand.of(
            draftId,
            userId,
            title,
            summary,
            category,
            thumbnailUrl,
            contentJson
        );
    }
}

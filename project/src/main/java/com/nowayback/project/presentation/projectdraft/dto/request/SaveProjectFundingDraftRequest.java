package com.nowayback.project.presentation.projectdraft.dto.request;

import com.nowayback.project.application.projectdraft.command.SaveFundingDraftCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "펀딩 드래프트 저장 요청")
public record SaveProjectFundingDraftRequest(

    @Schema(description = "목표 금액", example = "500000")
    @NotNull(message = "goalAmount는 필수입니다.")
    Long goalAmount,

    @Schema(description = "펀딩 시작일", example = "2025-01-01")
    LocalDate fundingStartDate,

    @Schema(description = "펀딩 종료일", example = "2025-01-31")
    LocalDate fundingEndDate
) {

    public SaveFundingDraftCommand toCommand(UUID draftId, UUID userId) {
        return SaveFundingDraftCommand.of(
            draftId,
            goalAmount,
            fundingStartDate,
            fundingEndDate
        );
    }
}

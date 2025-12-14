package com.nowayback.funding.presentation.funding.dto.request;

import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.CancelFundingCommand;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "후원 취소 요청 DTO")
public record CancelFundingRequest(

    @Schema(description = "취소 사유", example = "단순 변심")
    @NotBlank(message = "취소 사유는 필수입니다.")
    String reason
) {

    public CancelFundingCommand toCommand(UUID fundingId, UUID userId) {
        return new CancelFundingCommand(
            fundingId,
            userId,
            reason
        );
    }
}

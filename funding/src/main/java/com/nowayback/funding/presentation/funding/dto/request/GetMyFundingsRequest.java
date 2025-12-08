package com.nowayback.funding.presentation.funding.dto.request;

import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.GetMyFundingsCommand;
import com.nowayback.funding.domain.funding.entity.FundingStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 후원 내역 조회 요청 DTO")
public record GetMyFundingsRequest(

    @Schema(description = "후원 상태 필터", example = "COMPLETED")
    FundingStatus status,

    @Schema(description = "조회 기간", example = "LAST_30_DAYS")
    GetMyFundingsCommand.FundingPeriod period,

    @Schema(description = "정렬 기준", example = "LATEST")
    GetMyFundingsCommand.FundingSortBy sortBy,

    @Schema(description = "페이지 번호", example = "0")
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    Integer page,

    @Schema(description = "페이지 크기", example = "20")
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
    Integer size
) {

    public GetMyFundingsCommand toCommand(UUID userId) {
        return new GetMyFundingsCommand(
            userId,
            status,
            period != null ? period : GetMyFundingsCommand.FundingPeriod.ALL,
            sortBy != null ? sortBy : GetMyFundingsCommand.FundingSortBy.LATEST,
            page != null ? page : 0,
            size != null ? size : 20
        );
    }
}

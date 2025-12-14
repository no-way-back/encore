package com.nowayback.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

@Schema(description = "유저 정보 수정 요청")
public record UpdateUserInfoRequest (
        @Schema(description = "유저 ID", example = "000e0000-0000-0000-0000-000000000000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "유저 ID는 필수입니다.")
        UUID userId,

        @Schema(description = "닉네임", example = "유저수정", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣]{2,20}$",
                message = "닉네임은 2-20자의 한글, 영문, 숫자만 가능합니다."
        )
        String nickname
) {
}

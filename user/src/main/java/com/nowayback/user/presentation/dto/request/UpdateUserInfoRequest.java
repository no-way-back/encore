package com.nowayback.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record UpdateUserInfoRequest (
        @NotNull(message = "유저 ID는 필수입니다.")
        UUID userId,

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣]{2,20}$",
                message = "닉네임은 2-20자의 한글, 영문, 숫자만 가능합니다."
        )
        String nickname
) {
}

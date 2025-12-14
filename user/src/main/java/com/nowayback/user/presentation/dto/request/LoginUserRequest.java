package com.nowayback.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record LoginUserRequest (

        @Schema(description = "아이디", example = "user", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "아이디를 입력해주세요.")
        String username,

        @Schema(description = "비밀번호", example = "password123!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}

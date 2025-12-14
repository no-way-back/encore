package com.nowayback.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "사용자 이메일 수정 요청")
public record UpdateUserEmailRequest (
        @Schema(description = "이메일", example = "modified@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email
) {
}

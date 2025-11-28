package com.nowayback.user.presentation.dto.request;

import com.nowayback.user.domain.vo.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignupUserRequest (
        @NotBlank(message = "아이디를 입력해주세요.")
        @Pattern(
                regexp = "^[a-z0-9_-]{4,20}$",
                message = "아이디는 4-20자의 영문 소문자, 숫자, 특수기호(_, -) 만 가능합니다."
        )
        String username,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,20}$",
                message = "비밀번호는 8-20자이며, 영문, 숫자, 특수문자(@$!%*?&#)를 모두 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣]{2,20}$",
                message = "닉네임은 2-20자의 한글, 영문, 숫자만 가능합니다."
        )
        String nickname,

        @NotNull(message = "역할을 선택해주세요.")
        UserRole role
) {
}

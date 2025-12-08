package com.nowayback.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.user.application.AuthService;
import com.nowayback.user.application.dto.command.LoginUserCommand;
import com.nowayback.user.application.dto.result.LoginResult;
import com.nowayback.user.presentation.dto.request.SignupUserRequest;
import com.nowayback.user.presentation.exception.UserExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.nowayback.user.fixture.UserFixture.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("인증 컨트롤러")
@WebMvcTest(AuthController.class)
@Import(UserExceptionHandler.class)
class AuthControllerTest {

    @MockitoBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/auth";

    @Nested
    @DisplayName("회원가입")
    class Signup {

        @Test
        @DisplayName("유효한 요청이 들어오면 회원가입에 성공한다.")
        void signup_validRequest_success() throws Exception {
            /* given */
            SignupUserRequest request = VALID_SIGNUP_USER_REQUEST;

            /* when */
            /* then */
            mockMvc.perform(post(BASE_URL + "/signup")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("유효하지 않은 요청이 들어오면 응답코드 400을 반환한다.")
        void signup_invalidRequest_badRequest() throws Exception {
            /* given */
            SignupUserRequest request = INVALID_SIGNUP_USER_REQUEST;

            /* when */
            /* then */
            mockMvc.perform(post(BASE_URL + "/signup")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("유효한 요청이 들어오면 로그인을 성공한다.")
        void login_validRequest_success() throws Exception {
            /* given */
            LoginResult result = LOGIN_RESULT;

            given(authService.login(any(LoginUserCommand.class)))
                    .willReturn(result);

            /* when */
            /* then */
            mockMvc.perform(post(BASE_URL + "/login")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(VALID_LOGIN_USER_REQUEST)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value(result.accessToken()))
                    .andExpect(jsonPath("$.username").value(result.userResult().username()));
        }

        @Test
        @DisplayName("유효하지 않은 요청이 들어오면 응답코드 400을 반환한다.")
        void login_invalidRequest_badRequest() throws Exception {
            /* given */
            /* when */
            /* then */
            mockMvc.perform(post(BASE_URL + "/login")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(INVALID_LOGIN_USER_REQUEST)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("유효한 요청이 들어오면 로그아웃에 성공한다.")
        void logout_validRequest_success() throws Exception {
            /* given */
            String authHeader = "Bearer accessToken";

            /* when */
            /* then */
            mockMvc.perform(post(BASE_URL + "/logout")
                            .header("Authorization", authHeader))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("인증되지 않은 요청이 들어오면 응답코드 400을 반환한다.")
        void logout_unauthenticatedRequest_badRequest() throws Exception {
            /* given */
            /* when */
            /* then */
            mockMvc.perform(post(BASE_URL + "/logout"))
                    .andExpect(status().isBadRequest());
        }
    }
}
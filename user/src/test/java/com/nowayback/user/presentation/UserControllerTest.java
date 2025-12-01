package com.nowayback.user.presentation;

import com.nowayback.user.application.UserService;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static com.nowayback.user.fixture.UserFixture.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("유저 컨트롤러")
@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerTest {

    @MockitoBean
    private UserService userService;

    private static final String BASE_URL = "/users";

    @Nested
    @DisplayName("유저 단건 조회")
    class GetUser {

        @Test
        @DisplayName("유효한 요청이 들어오면 유저를 단건 조회한다.")
        void getUser_validRequest_success() throws Exception {
            /* given */
            UUID userId = USER_UUID;

            given(userService.getUser(any(UUID.class), any(UUID.class), anyString()))
                    .willReturn(USER_RESULT);

            /* when */
            /* then */
            performWithAuth(get(BASE_URL + "/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(USERNAME))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.role").value(UserRole.USER.name()))
                    .andExpect(jsonPath("$.status").value(UserStatus.ACTIVE.name()));
        }

        @Test
        @DisplayName("인증되지 않은 요청이 들어오면 응답코드 401을 반환한다.")
        void getUser_unauthorizedRequest_unauthorized() throws Exception {
            /* given */
            /* when */
            /* then */
            mockMvc.perform(get(BASE_URL + "/{userId}", USER_UUID))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("유저 목록 조회")
    class GetUsers {

        @ParameterizedTest(name = "{0} 권한으로 유저 목록 조회")
        @DisplayName("유효한 권한으로 유저 목록 조회 요청이 들어오면 유저 목록을 조회한다.")
        @EnumSource(value = UserRole.class, names = {"MASTER", "ADMIN"})
        void getUsers_validRole_success(UserRole role) throws Exception {
            /* given */
            given(userService.getUsers(any())).willReturn(USER_RESULT_PAGE);

            /* when */
            /* then */
            performWithAuth(post(BASE_URL + "/search")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(VALID_SEARCH_USER_REQUEST)), role.name())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].username").value(USERNAME));
        }

        @ParameterizedTest(name = "{0} 권한으로 유저 목록 조회")
        @DisplayName("유효하지 않은 권한으로 유저 목록 조회 요청이 들어오면 응답코드 403을 반환한다.")
        @EnumSource(value = UserRole.class, names = {"USER"})
        void getUsers_invalidRole_forbidden(UserRole role) throws Exception {
            /* given */
            /* when */
            /* then */
            performWithAuth(post(BASE_URL + "/search")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(VALID_SEARCH_USER_REQUEST)), role.name())
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("인증되지 않은 요청이 들어오면 응답코드 401을 반환한다.")
        void getUsers_unauthorizedRequest_unauthorized() throws Exception {
            /* given */
            /* when */
            /* then */
            mockMvc.perform(post(BASE_URL + "/search")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(VALID_SEARCH_USER_REQUEST)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("유저 이메일 수정")
    class UpdateEmail {

        @Test
        @DisplayName("유효한 요청이 들어오면 유저 이메일을 수정한다.")
        void updateEmail_validRequest_success() throws Exception {
            /* given */
            given(userService.updateEmail(anyString(), any(UUID.class)))
                    .willReturn(USER_RESULT);

            /* when */
            /* then */
            performWithAuth(patch(BASE_URL + "/email")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(VALID_UPDATE_USER_EMAIL_REQUEST)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(EMAIL));
        }

        @Test
        @DisplayName("유효하지 않은 요청이 들어오면 응답코드 400을 반환한다.")
        void updateEmail_invalidRequest_badRequest() throws Exception {
            /* given */
            /* when */
            /* then */
            performWithAuth(patch(BASE_URL + "/email")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(INVALID_UPDATE_USER_EMAIL_REQUEST)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("인증되지 않은 요청이 들어오면 응답코드 401을 반환한다.")
        void updateEmail_unauthorizedRequest_unauthorized() throws Exception {
            /* given */
            /* when */
            /* then */
            mockMvc.perform(patch(BASE_URL + "/email")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(VALID_UPDATE_USER_EMAIL_REQUEST)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("유저 정보 수정")
    class UpdateUserInfo {

        @Test
        @DisplayName("유효한 요청이 들어오면 유저 정보를 수정한다.")
        void updateUserInfo_validRequest_success() throws Exception {
            /* given */
            given(userService.updateUserInfo(any(), any(UUID.class), anyString()))
                    .willReturn(USER_RESULT);

            /* when */
            /* then */
            performWithAuth(patch(BASE_URL)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(VALID_UPDATE_USER_INFO_REQUEST)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nickname").value(NICKNAME));
        }

        @Test
        @DisplayName("유효하지 않은 요청이 들어오면 응답코드 400을 반환한다.")
        void updateUserInfo_invalidRequest_badRequest() throws Exception {
            /* given */
            /* when */
            /* then */
            performWithAuth(patch(BASE_URL)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(INVALID_UPDATE_USER_INFO_REQUEST)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("인증되지 않은 요청이 들어오면 응답코드 401을 반환한다.")
        void updateUserInfo_unauthorizedRequest_unauthorized() throws Exception {
            /* given */
            /* when */
            /* then */
            mockMvc.perform(patch(BASE_URL)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(VALID_UPDATE_USER_INFO_REQUEST)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("어드민 유저 승인")
    class ApproveAdmin {

        @ParameterizedTest
        @DisplayName("유효한 권한으로 어드민 유저 승인 요청이 들어오면 어드민 유저를 승인한다.")
        @EnumSource(value = UserRole.class, names = {"MASTER"})
        void approveAdmin_validRole_success(UserRole role) throws Exception {
            /* given */
            UUID userId = USER_UUID;

            given(userService.approveAdmin(any(UUID.class)))
                    .willReturn(USER_RESULT);

            /* when */
            /* then */
            performWithAuth(post(BASE_URL + "/{userId}/approve", userId), role.name())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value(UserRole.USER.name()));
        }

        @ParameterizedTest
        @DisplayName("유효하지 않은 권한으로 어드민 유저 승인 요청이 들어오면 응답코드 403을 반환한다.")
        @EnumSource(value = UserRole.class, names = {"ADMIN", "USER"})
        void approveAdmin_invalidRole_forbidden(UserRole role) throws Exception {
            /* given */
            /* when */
            /* then */
            performWithAuth(post(BASE_URL + "/{userId}/approve", USER_UUID), role.name())
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("인증되지 않은 요청이 들어오면 응답코드 401을 반환한다.")
        void approveAdmin_unauthorizedRequest_unauthorized() throws Exception {
            /* given */
            /* when */
            /* then */
            mockMvc.perform(post(BASE_URL + "/{userId}/approve", USER_UUID))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("유저 탈퇴")
    class DeactivateUser {

        @Test
        @DisplayName("유효한 요청이 들어오면 유저를 탈퇴시킨다.")
        void deactivateUser_validRequest_success() throws Exception {
            /* given */
            /* when */
            /* then */
            performWithAuth(delete(BASE_URL + "/{userId}", USER_UUID))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("인증되지 않은 요청이 들어오면 응답코드 401을 반환한다.")
        void deactivateUser_unauthorizedRequest_unauthorized() throws Exception {
            /* given */
            /* when */
            /* then */
            mockMvc.perform(delete(BASE_URL + "/{userId}", USER_UUID))
                    .andExpect(status().isUnauthorized());
        }
    }
}
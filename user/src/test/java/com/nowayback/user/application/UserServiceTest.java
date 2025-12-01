package com.nowayback.user.application;

import com.nowayback.user.application.dto.result.UserResult;
import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.exception.UserErrorCode;
import com.nowayback.user.domain.repository.UserRepository;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

import static com.nowayback.user.fixture.UserFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("유저 서비스")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("유저 단일 조회")
    class GetUser {

        @Test
        @DisplayName("본인을 조회할 시, 활성 상태인 경우 유저 정보를 반환한다.")
        void getUser_whenSelfAndActive_thenReturnUser() {
            /* given */
            User user = createUser(USER_UUID, UserStatus.ACTIVE, UserRole.USER);

            when(userRepository.findByIdAndStatusActive(user.getId()))
                    .thenReturn(Optional.of(user));

            /* when */
            UserResult result = userService.getUser(
                    USER_UUID,
                    USER_UUID,
                    UserRole.USER.name()
            );

            /* then */
            assertThat(result.username()).isEqualTo(user.getUsername());
        }

        @ParameterizedTest(name = "상태가 {0}인 경우 조회")
        @DisplayName("본인을 조회할 시, 비활성 상태인 경우 예외가 발생한다.")
        @EnumSource(value = UserStatus.class, names = {"PENDING", "SUSPENDED", "DEACTIVATED"})
        void getUser_whenSelfAndNotActive_thenThrowException(UserStatus status) {
            /* given */
            User user = createUser(USER_UUID, status, UserRole.USER);

            when(userRepository.findByIdAndStatusActive(user.getId()))
                    .thenReturn(Optional.empty());

            /* when */
            /* then */
            assertThatThrownBy(() -> userService.getUser(USER_UUID, USER_UUID, UserRole.USER.name()))
                    .isInstanceOf(Exception.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("본인이 아닌 유저를 조회할 시 예외가 발생한다.")
        void getUser_whenNotSelf_thenThrowException() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> userService.getUser(USER_UUID, UUID.randomUUID(), UserRole.USER.name()))
                    .isInstanceOf(Exception.class)
                    .hasMessage(UserErrorCode.FORBIDDEN_SELF_ACCESS.getMessage());
        }

        @ParameterizedTest(name = "역할이 {0}인 경우 조회")
        @DisplayName("ADMIN이나 MASTER가 타 유저를 조회할 시, 유저 정보를 반환한다.")
        @EnumSource(value = UserRole.class, names = {"ADMIN", "MASTER"})
        void getUser_whenAdminOrMaster_thenReturnUser(UserRole role) {
            /* given */
            User user = createUserWithStatusAndRole(UserStatus.SUSPENDED, UserRole.USER);

            when(userRepository.findById(user.getId()))
                    .thenReturn(Optional.of(user));

            /* when */
            UserResult result = userService.getUser(
                    user.getId(),
                    USER_UUID,
                    role.name()
            );

            /* then */
            assertThat(result.username()).isEqualTo(user.getUsername());
        }
    }

    @Nested
    @DisplayName("유저 목록 조회")
    class GetUsers {

        @Test
        @DisplayName("조건에 맞는 유저 목록 페이지를 반환한다.")
        void getUsers_whenFiltered_thenReturnUserPage() {
            /* given */
            User user = createUserWithStatusAndRole(UserStatus.ACTIVE, UserRole.ADMIN);

            when(userRepository.searchUser(any(), any(), any(), any(), any()))
                    .thenReturn(USER_PAGE);
            /* when */
            Page<UserResult> results = userService.getUsers(SEARCH_USER_QUERY);

            /* then */
            assertThat(results.getContent()).hasSize(USER_PAGE.getContent().size());
        }
    }

    @Nested
    @DisplayName("이메일 수정")
    class updateEmail {

        @Test
        @DisplayName("활성 상태인 경우 이메일이 수정된 유저 정보를 반환한다.")
        void updateEmail_whenSelfAndActive_thenReturnUpdatedUser() {
            /* given */
            User user = createUser(USER_UUID, UserStatus.ACTIVE, UserRole.USER);
            String newEmail = MODIFIED_EMAIL;

            when(userRepository.findByIdAndStatusActive(user.getId()))
                    .thenReturn(Optional.of(user));
            when(userRepository.existsByEmail(newEmail))
                    .thenReturn(false);

            /* when */
            UserResult result = userService.updateEmail(newEmail, USER_UUID);

            /* then */
            assertThat(result.email()).isEqualTo(newEmail);
        }

        @Test
        @DisplayName("새로운 이메일이 중복된 경우 예외가 발생한다.")
        void updateEmail_whenDuplicateEmail_thenThrowException() {
            /* given */
            User user = createUser(USER_UUID, UserStatus.ACTIVE, UserRole.USER);
            String newEmail = MODIFIED_EMAIL;

            when(userRepository.findByIdAndStatusActive(user.getId()))
                    .thenReturn(Optional.of(user));
            when(userRepository.existsByEmail(newEmail))
                    .thenReturn(true);

            /* when */
            /* then */
            assertThatThrownBy(() -> userService.updateEmail(newEmail, USER_UUID))
                    .isInstanceOf(Exception.class)
                    .hasMessage(UserErrorCode.DUPLICATE_EMAIL.getMessage());
        }

        @ParameterizedTest(name = "상태가 {0}인 경우 수정")
        @DisplayName("활성 상태가 아닌 경우 예외가 발생한다.")
        @EnumSource(value = UserStatus.class, names = {"PENDING", "SUSPENDED", "DEACTIVATED"})
        void updateEmail_whenNotActive_thenThrowException(UserStatus status) {
            /* given */
            User user = createUser(USER_UUID, status, UserRole.USER);
            String newEmail = MODIFIED_EMAIL;

            when(userRepository.findByIdAndStatusActive(user.getId()))
                    .thenReturn(Optional.empty());

            /* when */
            /* then */
            assertThatThrownBy(() -> userService.updateEmail(newEmail, USER_UUID))
                    .isInstanceOf(Exception.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("유저 정보 수정")
    class updateUserInfo {

        @Test
        @DisplayName("본인을 수정할 시, 유효한 정보로 요청하면 유저 정보를 반환한다.")
        void updateUserInfo_whenSelfAndValid_thenReturnUpdatedUser() {
            /* given */
            User user = createUser(USER_UUID, UserStatus.ACTIVE, UserRole.USER);
            String newNickname = MODIFIED_NICKNAME;

            when(userRepository.findByIdAndStatusActive(user.getId()))
                    .thenReturn(Optional.of(user));
            when(userRepository.existsByNickname(newNickname))
                    .thenReturn(false);

            /* when */
            UserResult result = userService.updateUserInfo(
                    UPDATE_USER_INFO_COMMAND,
                    USER_UUID,
                    UserRole.USER.name()
            );

            /* then */
            assertThat(result.nickname()).isEqualTo(newNickname);
        }

        @Test
        @DisplayName("본인이 아닌 유저를 수정할 시 예외가 발생한다.")
        void updateUserInfo_whenNotSelf_thenThrowException() {
            /* given */
            User user = createUser();

            /* when */
            /* then */
            assertThatThrownBy(() -> userService.updateUserInfo(UPDATE_USER_INFO_COMMAND, UUID.randomUUID(), UserRole.USER.name()))
                    .isInstanceOf(Exception.class)
                    .hasMessage(UserErrorCode.FORBIDDEN_SELF_ACCESS.getMessage());
        }

        @Test
        @DisplayName("중복된 닉네임인 경우 예외가 발생한다.")
        void updateUserInfo_whenDuplicateNickname_thenThrowException() {
            /* given */
            User user = createUser(USER_UUID, UserStatus.ACTIVE, UserRole.USER);

            when(userRepository.findByIdAndStatusActive(user.getId()))
                    .thenReturn(Optional.of(user));
            when(userRepository.existsByNickname(UPDATE_USER_INFO_COMMAND.nickname()))
                    .thenReturn(true);

            /* when */
            /* then */
            assertThatThrownBy(() -> userService.updateUserInfo(UPDATE_USER_INFO_COMMAND, USER_UUID, UserRole.USER.name()))
                    .isInstanceOf(Exception.class)
                    .hasMessage(UserErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        @ParameterizedTest(name = "상태가 {0}인 경우 수정")
        @DisplayName("활성 상태가 아닌 경우 예외가 발생한다.")
        @EnumSource(value = UserStatus.class, names = {"PENDING", "SUSPENDED", "DEACTIVATED"})
        void updateUserInfo_whenNotActive_thenThrowException(UserStatus status) {
            /* given */
            User user = createUser(USER_UUID, status, UserRole.USER);

            when(userRepository.findByIdAndStatusActive(user.getId()))
                    .thenReturn(Optional.empty());

            /* when */
            /* then */
            assertThatThrownBy(() -> userService.updateUserInfo(UPDATE_USER_INFO_COMMAND, USER_UUID, UserRole.USER.name()))
                    .isInstanceOf(Exception.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        @ParameterizedTest(name = "역할이 {0}인 경우 수정")
        @DisplayName("MASTER나 ADMIN이 타 유저를 수정할 시, 유저 정보를 반환한다.")
        @EnumSource(value = UserRole.class, names = {"ADMIN", "MASTER"})
        void updateUserInfo_whenAdminOrMaster_thenReturnUpdatedUser(UserRole role) {
            /* given */
            User user = createUser(USER_UUID, UserStatus.SUSPENDED, UserRole.USER);

            when(userRepository.findById(user.getId()))
                    .thenReturn(Optional.of(user));
            when(userRepository.existsByNickname(UPDATE_USER_INFO_COMMAND.nickname()))
                    .thenReturn(false);

            /* when */
            UserResult result = userService.updateUserInfo(
                    UPDATE_USER_INFO_COMMAND,
                    USER_UUID,
                    role.name()
            );

            /* then */
            assertThat(result.nickname()).isEqualTo(UPDATE_USER_INFO_COMMAND.nickname());
        }
    }

    @Nested
    @DisplayName("ADMIN 승인")
    class approveAdmin {

        @Test
        @DisplayName("MASTER가 ADMIN 승인을 할 시, 유저 정보를 반환한다.")
        void approveAdmin_whenMaster_thenReturnApprovedUser() {
            /* given */
            User user = createUserWithStatusAndRole(UserStatus.PENDING, UserRole.ADMIN);

            when(userRepository.findById(any()))
                    .thenReturn(Optional.of(user));

            /* when */
            UserResult result = userService.approveAdmin(USER_UUID);

            /* then */
            assertThat(result.status()).isEqualTo(UserStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("유저 탈퇴")
    class deactivateUser {

        @Test
        @DisplayName("본인이 탈퇴할 시, 활성 상태인 경우 정상 처리된다.")
        void deactivateUser_whenSelfAndActive_thenDeactivateSuccessfully() {
            /* given */
            User user = createUser(USER_UUID, UserStatus.ACTIVE, UserRole.USER);

            when(userRepository.findByIdAndStatusActive(user.getId()))
                    .thenReturn(Optional.of(user));

            /* when */
            userService.deactivateUser(
                    user.getId(),
                    user.getId(),
                    UserRole.USER.name()
            );

            /* then */
            verify(userRepository, times(1)).findByIdAndStatusActive(user.getId());
        }

        @Test
        @DisplayName("본인이 아닌 유저를 탈퇴할 시 예외가 발생한다.")
        void deactivateUser_whenNotSelf_thenThrowException() {
            /* given */
            User user = createUser(USER_UUID, UserStatus.ACTIVE, UserRole.USER);

            /* when */
            /* then */
            assertThatThrownBy(() -> userService.deactivateUser(user.getId(), UUID.randomUUID(), UserRole.USER.name()))
                    .isInstanceOf(Exception.class)
                    .hasMessage(UserErrorCode.FORBIDDEN_SELF_ACCESS.getMessage());
        }

        @ParameterizedTest(name = "상태가 {0}인 경우 탈퇴")
        @DisplayName("활성 상태가 아닌 경우 예외가 발생한다.")
        @EnumSource(value = UserStatus.class, names = {"PENDING", "SUSPENDED", "DEACTIVATED"})
        void deactivateUser_whenNotActive_thenThrowException(UserStatus status) {
            /* given */
            User user = createUser(USER_UUID, UserStatus.SUSPENDED, UserRole.USER);

            when(userRepository.findByIdAndStatusActive(user.getId()))
                    .thenReturn(Optional.empty());

            /* when */
            /* then */
            assertThatThrownBy(() -> userService.deactivateUser(user.getId(), user.getId(), UserRole.USER.name()))
                    .isInstanceOf(Exception.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("MASTER나 ADMIN이 타 유저를 탈퇴할 시, 정상 처리된다.")
        void deactivateUser_whenAdminOrMaster_thenDeactivateSuccessfully() {
            /* given */
            User user = createUser();

            when(userRepository.findById(any()))
                    .thenReturn(Optional.of(user));

            /* when */
            userService.deactivateUser(
                    user.getId(),
                    USER_UUID,
                    UserRole.ADMIN.name()
            );

            /* then */
            verify(userRepository, times(1)).findById(any());
        }

        @Test
        @DisplayName("MASTER나 ADMIN이 타 유저를 탈퇴할 시, 탈퇴 대상 유저가 존재하지 않으면 예외가 발생한다.")
        void deactivateUser_whenAdminOrMasterAndUserNotFound_thenThrowException() {
            /* given */
            User user = createUser();

            when(userRepository.findById(any()))
                    .thenReturn(Optional.empty());

            /* when */
            /* then */
            assertThatThrownBy(() -> userService.deactivateUser(user.getId(), USER_UUID, UserRole.ADMIN.name()))
                    .isInstanceOf(Exception.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }
    }
}
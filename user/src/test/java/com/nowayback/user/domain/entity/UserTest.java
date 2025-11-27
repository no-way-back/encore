package com.nowayback.user.domain.entity;

import com.nowayback.user.domain.exception.UserDomainErrorCode;
import com.nowayback.user.domain.exception.UserDomainException;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static com.nowayback.user.fixture.UserFixture.*;

@DisplayName("유저 엔티티")
class UserTest {

    @Nested
    @DisplayName("유저 엔티티 생성")
    class Create {

        @ParameterizedTest(name = "{0} 역할로 유저 엔티티 생성 시도")
        @DisplayName("모든 필드가 유효하면 유저 엔티티 생성에 성공한다.")
        @EnumSource(value = UserRole.class, names = {"ADMIN", "USER"})
        void create_givenValidFields_thenSuccess(UserRole role) {
            /* given */
            String username = USERNAME;
            String password = ENCODED_PASSWORD;
            String email = EMAIL;
            String nickname = NICKNAME;

            /* when */
            User user = User.create(username, password, email, nickname, role);

            /* then */
            assertThat(user.getUsername()).isEqualTo(username);
            assertThat(user.getPassword()).isEqualTo(password);
            assertThat(user.getEmail()).isEqualTo(email);
            assertThat(user.getNickname()).isEqualTo(nickname);
            assertThat(user.getRole()).isEqualTo(role);
            assertThat(user.getStatus()).isEqualTo(role.getInitialStatus());
        }

        @Test
        @DisplayName("MASTER 역할로 유저 엔티티 생성을 시도하면 예외가 발생한다.")
        void create_givenMasterRole_thenThrowException() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> User.create(USERNAME, ENCODED_PASSWORD, EMAIL, NICKNAME, UserRole.MASTER))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage(UserDomainErrorCode.INVALID_USER_ROLE_FOR_CREATION.getMessage());
        }
    }

    @Nested
    @DisplayName("유저 상태 변경")
    class ChangeStatus {

        @ParameterizedTest(name = "{0} 상태에서 {1} 상태로 변경 시도")
        @DisplayName("유효한 상태 전이 시 상태가 변경된다.")
        @MethodSource("provideValidStatusTransitions")
        void changeStatus_givenValidTransition_thenSuccess(UserStatus initialStatus, UserStatus newStatus) {
            /* given */
            User user = createUserWithStatus(initialStatus);

            /* when */
            user.changeStatus(newStatus);

            /* then */
            assertThat(user.getStatus()).isEqualTo(newStatus);
        }

        @ParameterizedTest(name = "{0} 상태에서 {1} 상태로 변경 시도")
        @DisplayName("유효하지 않은 상태 전이 시 예외가 발생한다.")
        @MethodSource("provideInvalidStatusTransitions")
        void changeStatus_givenInvalidTransition_thenThrowException(UserStatus initialStatus, UserStatus newStatus) {
            /* given */
            User user = createUserWithStatus(initialStatus);

            /* when */
            /* then */
            assertThatThrownBy(() -> user.changeStatus(newStatus))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage(UserDomainErrorCode.INVALID_USER_STATUS_TRANSITION.getMessage());
        }

        private static Stream<Arguments> provideValidStatusTransitions() {
            return Stream.of(
                    /* PENDING -> ACTIVE, DEACTIVATED */
                    Arguments.of(UserStatus.PENDING, UserStatus.ACTIVE),
                    Arguments.of(UserStatus.PENDING, UserStatus.DEACTIVATED),

                    /* ACTIVE -> SUSPENDED, DEACTIVATED */
                    Arguments.of(UserStatus.ACTIVE, UserStatus.SUSPENDED),
                    Arguments.of(UserStatus.ACTIVE, UserStatus.DEACTIVATED),

                    /* SUSPENDED -> ACTIVE, DEACTIVATED */
                    Arguments.of(UserStatus.SUSPENDED, UserStatus.ACTIVE),
                    Arguments.of(UserStatus.SUSPENDED, UserStatus.DEACTIVATED),

                    /* DEACTIVATED -> ACTIVE */
                    Arguments.of(UserStatus.DEACTIVATED, UserStatus.ACTIVE)
            );
        }

        private static Stream<Arguments> provideInvalidStatusTransitions() {
            return Stream.of(
                    /* PENDING -> SUSPENDED */
                    Arguments.of(UserStatus.PENDING, UserStatus.SUSPENDED),
                    Arguments.of(UserStatus.PENDING, UserStatus.PENDING),

                    /* ACTIVE -> PENDING */
                    Arguments.of(UserStatus.ACTIVE, UserStatus.PENDING),
                    Arguments.of(UserStatus.ACTIVE, UserStatus.ACTIVE),

                    /* SUSPENDED -> PENDING */
                    Arguments.of(UserStatus.SUSPENDED, UserStatus.PENDING),
                    Arguments.of(UserStatus.SUSPENDED, UserStatus.SUSPENDED),

                    /* DEACTIVATED -> PENDING, SUSPENDED, DEACTIVATED */
                    Arguments.of(UserStatus.DEACTIVATED, UserStatus.PENDING),
                    Arguments.of(UserStatus.DEACTIVATED, UserStatus.SUSPENDED),
                    Arguments.of(UserStatus.DEACTIVATED, UserStatus.DEACTIVATED)
            );
        }
    }

    @Nested
    @DisplayName("유저 승인")
    class Approve {

        @Test
        @DisplayName("ADMIN 역할의 PENDING 상태 유저를 승인하면 ACTIVE 상태로 변경된다.")
        void approve_givenAdminPendingUser_thenSuccess() {
            /* given */
            User user = createUser(UserRole.ADMIN);

            /* when */
            user.approve();

            /* then */
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @ParameterizedTest(name = "{0} 역할의 유저 승인 시도")
        @DisplayName("ADMIN 역할이 아닌 유저를 승인 시도하면 예외가 발생한다.")
        @EnumSource(value = UserRole.class, names = {"USER", "MASTER"})
        void approve_givenNonAdminUser_thenThrowException(UserRole role) {
            /* given */
            User user = createUserWithRole(role);

            /* when */
            /* then */
            assertThatThrownBy(user::approve)
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage(UserDomainErrorCode.INVALID_USER_ROLE_FOR_APPROVAL.getMessage());
        }

        @ParameterizedTest(name = "{0} 상태의 ADMIN 유저 승인 시도")
        @DisplayName("PENDING 상태가 아닌 ADMIN 유저를 승인 시도하면 예외가 발생한다.")
        @EnumSource(value = UserStatus.class, names = {"ACTIVE", "SUSPENDED", "DEACTIVATED"})
        void approve_givenNonPendingAdminUser_thenThrowException(UserStatus status) {
            /* given */
            User user = createUserWithStatusAndRole(status, UserRole.ADMIN);

            /* when */
            /* then */
            assertThatThrownBy(user::approve)
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage(UserDomainErrorCode.INVALID_USER_STATUS_FOR_APPROVAL.getMessage());
        }
    }

    @Nested
    @DisplayName("유저 비활성화")
    class Deactivate {

        @ParameterizedTest(name = "{0} 상태의 유저 비활성화 시도")
        @DisplayName("유저 비활성화 시 상태가 DEACTIVATED로 변경된다.")
        @EnumSource(value = UserStatus.class, names = {"PENDING", "ACTIVE", "SUSPENDED"})
        void deactivate_givenActiveUser_thenSuccess(UserStatus status) {
            /* given */
            User user = createUserWithStatus(status);

            /* when */
            user.deactivate(USER_UUID);

            /* then */
            assertThat(user.getStatus()).isEqualTo(UserStatus.DEACTIVATED);
            assertThat(user.getDeletedAt()).isNotNull();
            assertThat(user.getDeletedBy()).isEqualTo(USER_UUID);
        }

        @Test
        @DisplayName("이미 DEACTIVATED 상태인 유저를 비활성화 시도하면 예외가 발생한다.")
        void deactivate_givenDeactivatedUser_thenThrowException() {
            /* given */
            User user = createUserWithStatus(UserStatus.DEACTIVATED);

            /* when */
            /* then */
            assertThatThrownBy(() -> user.deactivate(USER_UUID))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage(UserDomainErrorCode.USER_ALREADY_DEACTIVATED.getMessage());
        }
    }
}
package com.nowayback.user.application;

import com.nowayback.user.application.dto.result.LoginResult;
import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.exception.UserErrorCode;
import com.nowayback.user.domain.exception.UserException;
import com.nowayback.user.domain.repository.UserRepository;
import com.nowayback.user.domain.vo.UserStatus;
import com.nowayback.user.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.nowayback.user.fixture.UserFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("인증 서비스")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("회원가입")
    class Signup {

        @Test
        @DisplayName("유효한 정보로 회원가입을 하면 회원이 생성된다.")
        void signup_whenValid_thenCreateUser() {
            /* given */
            when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(userRepository.existsByNickname(NICKNAME)).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);

            /* when */
            authService.signup(SIGNUP_USER_COMMAND);

            /* then */
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());

            User savedUser = captor.getValue();
            assertThat(savedUser.getUsername()).isEqualTo(USERNAME);
            assertThat(savedUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
            assertThat(savedUser.getEmail()).isEqualTo(EMAIL);
            assertThat(savedUser.getNickname()).isEqualTo(NICKNAME);
        }

        @Test
        @DisplayName("이미 존재하는 유저 이름일 경우 예외가 발생한다.")
        void signup_whenDuplicateUsername_thenThrowException() {
            /* given */
            when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

            /* when */
            /* then */
            assertThatThrownBy(() -> authService.signup(SIGNUP_USER_COMMAND))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.DUPLICATE_USERNAME.getMessage());
        }

        @Test
        @DisplayName("이미 존재하는 이메일일 경우 예외가 발생한다.")
        void signup_whenDuplicateEmail_thenThrowException() {
            /* given */
            when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
            when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

            /* when */
            /* then */
            assertThatThrownBy(() -> authService.signup(SIGNUP_USER_COMMAND))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.DUPLICATE_EMAIL.getMessage());
        }

        @Test
        @DisplayName("이미 존재하는 닉네임일 경우 예외가 발생한다.")
        void signup_whenDuplicateNickname_thenThrowException() {
            /* given */
            when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(userRepository.existsByNickname(NICKNAME)).thenReturn(true);

            /* when */
            /* then */
            assertThatThrownBy(() -> authService.signup(SIGNUP_USER_COMMAND))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.DUPLICATE_NICKNAME.getMessage());
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("유효한 정보로 로그인을 요청하면 토큰과 유저 정보를 반환한다.")
        void login_whenValid_thenReturnTokenAndUser() {
            /* given */
            User user = createUserWithStatus(UserStatus.ACTIVE);

            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(true);
            when(tokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole()))
                    .thenReturn(ACCESS_TOKEN);

            /* when */
            LoginResult result = authService.login(LOGIN_USER_COMMAND);

            /* then */
            assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(result.userResult().username()).isEqualTo(user.getUsername());
        }

        @Test
        @DisplayName("존재하지 않는 유저 이름일 경우 예외가 발생한다.")
        void login_whenNonexistentUsername_thenThrowException() {
            /* given */
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

            /* when */
            /* then */
            assertThatThrownBy(() -> authService.login(LOGIN_USER_COMMAND))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_CREDENTIALS.getMessage());
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않을 경우 예외가 발생한다.")
        void login_whenInvalidPassword_thenThrowException() {
            /* given */
            User user = createUserWithStatus(UserStatus.ACTIVE);

            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(false);

            /* when */
            /* then */
            assertThatThrownBy(() -> authService.login(LOGIN_USER_COMMAND))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_CREDENTIALS.getMessage());
        }

        @Test
        @DisplayName("가입 대기중인 유저가 로그인을 시도할 경우 예외가 발생한다.")
        void login_whenPendingUser_thenThrowException() {
            /* given */
            User user = createUserWithStatus(UserStatus.PENDING);

            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(true);

            /* when */
            /* then */
            assertThatThrownBy(() -> authService.login(LOGIN_USER_COMMAND))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.USER_STATUS_PENDING.getMessage());
        }

        @Test
        @DisplayName("정지된 유저가 로그인을 시도할 경우 예외가 발생한다.")
        void login_whenSuspendedUser_thenThrowException() {
            /* given */
            User user = createUserWithStatus(UserStatus.SUSPENDED);

            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(true);

            /* when */
            /* then */
            assertThatThrownBy(() -> authService.login(LOGIN_USER_COMMAND))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.USER_STATUS_SUSPENDED.getMessage());
        }
    }
}
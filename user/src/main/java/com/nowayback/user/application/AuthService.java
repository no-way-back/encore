package com.nowayback.user.application;

import com.nowayback.user.application.dto.command.LoginUserCommand;
import com.nowayback.user.application.dto.command.SignupUserCommand;
import com.nowayback.user.application.dto.result.LoginResult;
import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.exception.UserErrorCode;
import com.nowayback.user.domain.exception.UserException;
import com.nowayback.user.domain.repository.UserRepository;
import com.nowayback.user.domain.vo.UserStatus;
import com.nowayback.user.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public void signup(SignupUserCommand command) {
        String username = command.username();
        String email = command.email();
        String nickname = command.nickname();

        validateDuplicateUser(username, email, nickname);

        User user = User.create(
                username,
                passwordEncoder.encode(command.password()),
                email,
                nickname,
                command.role()
        );

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResult login(LoginUserCommand command) {
        User user = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new UserException(UserErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_CREDENTIALS);
        }

        validateUserStatusActive(user);

        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        return LoginResult.from(accessToken, user);
    }

    private void validateDuplicateUser(String username, String email, String nickname) {
        validateDuplicateUsername(username);
        validateDuplicateEmail(email);
        validateDuplicateNickname(nickname);
    }

    private void validateDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserException(UserErrorCode.DUPLICATE_USERNAME);
        }
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateDuplicateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validateUserStatusActive(User user) {
        if (user.getStatus() == UserStatus.PENDING) {
            throw new UserException(UserErrorCode.USER_STATUS_PENDING);
        } else if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new UserException(UserErrorCode.USER_STATUS_SUSPENDED);
        }
    }
}

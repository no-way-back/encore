package com.nowayback.user.presentation;

import com.nowayback.user.application.AuthService;
import com.nowayback.user.application.dto.command.LoginUserCommand;
import com.nowayback.user.application.dto.command.SignupUserCommand;
import com.nowayback.user.presentation.dto.request.LoginUserRequest;
import com.nowayback.user.presentation.dto.request.SignupUserRequest;
import com.nowayback.user.presentation.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
            @Valid @RequestBody SignupUserRequest request
    ) {
        SignupUserCommand command = new SignupUserCommand(
                request.username(),
                request.password(),
                request.email(),
                request.nickname(),
                request.role()
        );

        authService.signup(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginUserRequest request
    ) {
        LoginUserCommand command =LoginUserCommand.of(
                request.username(),
                request.password()
        );

        LoginResponse response = LoginResponse.from(authService.login(command));

        return ResponseEntity.ok(response);
    }
}

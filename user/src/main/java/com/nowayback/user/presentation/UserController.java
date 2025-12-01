package com.nowayback.user.presentation;

import com.nowayback.user.application.UserService;
import com.nowayback.user.application.dto.command.UpdateUserInfoCommand;
import com.nowayback.user.application.dto.query.SearchUserQuery;
import com.nowayback.user.application.dto.result.UserResult;
import com.nowayback.user.presentation.auth.role.RequiredRole;
import com.nowayback.user.presentation.auth.user.AuthUser;
import com.nowayback.user.presentation.auth.user.CurrentUser;
import com.nowayback.user.presentation.dto.request.SearchUserRequest;
import com.nowayback.user.presentation.dto.request.UpdateUserEmailRequest;
import com.nowayback.user.presentation.dto.request.UpdateUserInfoRequest;
import com.nowayback.user.presentation.dto.response.PageResponse;
import com.nowayback.user.presentation.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @CurrentUser AuthUser authUser,
            @PathVariable("userId") UUID userId
    ) {
        UserResult result = userService.getUser(userId, authUser.userId(), authUser.role());
        return ResponseEntity.ok(UserResponse.from(result));
    }

    @PostMapping("/search")
    @RequiredRole({"MASTER", "ADMIN"})
    public ResponseEntity<PageResponse<UserResponse>> getUsers(
            @RequestBody SearchUserRequest request,
            @PageableDefault Pageable pageable
    ) {
        SearchUserQuery query = SearchUserQuery.of(
                request.keyword(),
                request.searchFields(),
                request.role(),
                request.status(),
                pageable
        );

        Page<UserResult> results = userService.getUsers(query);

        return ResponseEntity.ok(PageResponse.from(results.map(UserResponse::from)));
    }

    @PatchMapping("/email")
    public ResponseEntity<UserResponse> updateEmail(
            @CurrentUser AuthUser authUser,
            @Valid @RequestBody UpdateUserEmailRequest request
    ) {
        UserResult result = userService.updateEmail(request.email(), authUser.userId());
        return ResponseEntity.ok(UserResponse.from(result));
    }

    @PatchMapping
    public ResponseEntity<UserResponse> updateInfo(
            @CurrentUser AuthUser authUser,
            @Valid @RequestBody UpdateUserInfoRequest request
    ) {
        UpdateUserInfoCommand command = UpdateUserInfoCommand.of(
                request.userId(),
                request.nickname()
        );

        UserResult result = userService.updateUserInfo(
                command,
                authUser.userId(),
                authUser.role()
        );

        return ResponseEntity.ok(UserResponse.from(result));
    }

    @PostMapping("/{userId}/approve")
    @RequiredRole({"MASTER"})
    public ResponseEntity<UserResponse> approveAdmin(
            @PathVariable("userId") UUID userId
    ) {
        UserResult result = userService.approveAdmin(userId);
        return ResponseEntity.ok(UserResponse.from(result));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deactivateUser(
            @CurrentUser AuthUser authUser,
            @PathVariable("userId") UUID userId
    ) {
        userService.deactivateUser(userId, authUser.userId(), authUser.role());
        return ResponseEntity.noContent().build();
    }
}

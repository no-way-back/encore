package com.nowayback.user.application;

import com.nowayback.user.application.dto.command.UpdateUserInfoCommand;
import com.nowayback.user.application.dto.query.SearchUserQuery;
import com.nowayback.user.application.dto.result.UserResult;
import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.exception.UserErrorCode;
import com.nowayback.user.domain.exception.UserException;
import com.nowayback.user.domain.repository.UserRepository;
import com.nowayback.user.domain.vo.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final int MAX_PAGE_SIZE = 50;

    @Transactional(readOnly = true)
    public UserResult getUser(UUID userId, UUID requesterId, String role) {
        if (isMasterOrAdmin(role)) {
            return UserResult.from(getUserById(userId));
        } else {
            validateSelf(userId, requesterId);
            return UserResult.from(getActiveUserById(userId));
        }
    }

    @Transactional(readOnly = true)
    public Page<UserResult> getUsers(SearchUserQuery query) {
        Page<User> users = userRepository.searchUser(
                query.keyword(),
                query.searchFields(),
                query.role(),
                query.status(),
                getSafePageable(query.pageable())
        );

        return users.map(UserResult::from);
    }

    @Transactional
    public UserResult updateEmail(String newEmail, UUID requesterId) {
        User user = getActiveUserById(requesterId);

        validateDuplicateEmail(newEmail);
        user.updateEmail(newEmail);

        return UserResult.from(user);
    }

    @Transactional
    public UserResult updateUserInfo(UpdateUserInfoCommand command, UUID requesterId, String role) {
        User user = getUserByRoleAndPermission(command.userId(), requesterId, role);

        validateDuplicateNickname(command.nickname());
        user.updateInfo(command.nickname());

        return UserResult.from(user);
    }

    @Transactional
    public UserResult approveAdmin(UUID userId) {
        User user = getUserById(userId);
        user.approve();
        return UserResult.from(user);
    }

    @Transactional
    public void deactivateUser(UUID userId, UUID requesterId, String role) {
        User user = getUserByRoleAndPermission(userId, requesterId, role);
        user.deactivate(requesterId);
    }

    private User getUserByRoleAndPermission(UUID userId, UUID requesterId, String role) {
        boolean isPrivileged = isMasterOrAdmin(role);

        if (!isPrivileged) {
            validateSelf(userId, requesterId);
        }

        return isPrivileged ? getUserById(userId) : getActiveUserById(userId);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private User getActiveUserById(UUID userId) {
        return userRepository.findByIdAndStatusActive(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Pageable getSafePageable(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }

    private boolean isMasterOrAdmin(String role) {
        return role.equals(UserRole.MASTER.name()) || role.equals(UserRole.ADMIN.name());
    }

    private void validateSelf(UUID userId, UUID requesterId) {
        if (!userId.equals(requesterId)) {
            throw new UserException(UserErrorCode.FORBIDDEN_SELF_ACCESS);
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
}

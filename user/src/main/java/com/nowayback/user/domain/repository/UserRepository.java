package com.nowayback.user.domain.repository;

import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID userId);
    Optional<User> findByIdAndStatusActive(UUID userId);
    Page<User> searchUser(String keyword, List<String> strings, UserRole role, UserStatus status, Pageable safePageable);
}

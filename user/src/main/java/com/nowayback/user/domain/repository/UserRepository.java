package com.nowayback.user.domain.repository;

import com.nowayback.user.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByUsername(String username);
}

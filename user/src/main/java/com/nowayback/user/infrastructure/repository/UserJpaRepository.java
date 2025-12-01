package com.nowayback.user.infrastructure.repository;

import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.vo.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, UUID> {
    boolean existsByUsernameAndDeletedAtIsNull(String username);
    boolean existsByEmailAndDeletedAtIsNull(String email);
    boolean existsByNicknameAndDeletedAtIsNull(String nickname);
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
    Optional<User> findByIdAndStatus(UUID userId, UserStatus status);
}

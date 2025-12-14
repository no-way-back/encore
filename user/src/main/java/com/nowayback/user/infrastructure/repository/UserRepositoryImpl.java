package com.nowayback.user.infrastructure.repository;

import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.repository.UserRepository;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserCustomRepository customRepository;

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return jpaRepository.existsByNicknameAndDeletedAtIsNull(nickname);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return jpaRepository.findById(userId);
    }

    @Override
    public Optional<User> findByIdAndStatusActive(UUID userId) {
        return jpaRepository.findByIdAndStatus(userId, UserStatus.ACTIVE);
    }

    @Override
    public Page<User> searchUser(String keyword, List<String> strings, UserRole role, UserStatus status, Pageable safePageable) {
        return customRepository.searchUser(keyword, strings, role, status, safePageable);
    }
}

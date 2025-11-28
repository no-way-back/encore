package com.nowayback.user.infrastructure.repository;

import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.repository.UserRepository;
import com.nowayback.user.domain.vo.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

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
}

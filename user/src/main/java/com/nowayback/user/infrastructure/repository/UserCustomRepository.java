package com.nowayback.user.infrastructure.repository;

import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserCustomRepository {
    Page<User> searchUser(String keyword, List<String> searchFields, UserRole role, UserStatus status, Pageable pageable);
}

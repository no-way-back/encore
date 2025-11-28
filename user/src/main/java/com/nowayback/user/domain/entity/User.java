package com.nowayback.user.domain.entity;

import com.nowayback.user.domain.BaseEntity;
import com.nowayback.user.domain.exception.UserErrorCode;
import com.nowayback.user.domain.exception.UserException;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 20)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "nickname", unique = true, nullable = false, length = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    private User(String username, String password, String email, String nickname, UserRole role, UserStatus status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.status = status;
    }

    /* Factory Method */
    public static User create(String username, String password, String email, String nickname, UserRole role) {
        validateCreatableRole(role);
        UserStatus initialStatus = role.getInitialStatus();
        return new User(username, password, email, nickname, role, initialStatus);
    }

    private static void validateCreatableRole(UserRole role) {
        if (!role.canBeCreatedDirectly()) {
            throw new UserException(UserErrorCode.INVALID_USER_ROLE_FOR_CREATION);
        }
    }

    /* Business Methods */
    public void changeStatus(UserStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new UserException(UserErrorCode.INVALID_USER_STATUS_TRANSITION);
        }
        this.status = newStatus;
    }

    public void approve() {
        if (this.role != UserRole.ADMIN) {
            throw new UserException(UserErrorCode.INVALID_USER_ROLE_FOR_APPROVAL);
        } else if (this.status != UserStatus.PENDING) {
            throw new UserException(UserErrorCode.INVALID_USER_STATUS_FOR_APPROVAL);
        } else {
            changeStatus(UserStatus.ACTIVE);
        }
    }

    public void deactivate(UUID deletedBy) {
        if (this.status == UserStatus.DEACTIVATED) {
            throw new UserException(UserErrorCode.USER_ALREADY_DEACTIVATED);
        }
        softDelete(deletedBy);
        changeStatus(UserStatus.DEACTIVATED);
    }
}
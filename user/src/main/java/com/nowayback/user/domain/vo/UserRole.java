package com.nowayback.user.domain.vo;

import com.nowayback.user.domain.exception.UserDomainErrorCode;
import com.nowayback.user.domain.exception.UserDomainException;

public enum UserRole {
    MASTER {
        @Override
        public UserStatus getInitialStatus() {
            throw new UserDomainException(UserDomainErrorCode.INVALID_USER_ROLE_FOR_CREATION);
        }

        @Override
        public boolean canBeCreatedDirectly() {
            return false;
        }
    },
    ADMIN {
        @Override
        public UserStatus getInitialStatus() {
            return UserStatus.PENDING;
        }

        @Override
        public boolean canBeCreatedDirectly() {
            return true;
        }
    },
    USER {
        @Override
        public UserStatus getInitialStatus() {
            return UserStatus.ACTIVE;
        }

        @Override
        public boolean canBeCreatedDirectly() {
            return true;
        }
    };

    public abstract UserStatus getInitialStatus();
    public abstract boolean canBeCreatedDirectly();
}

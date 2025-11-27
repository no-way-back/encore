package com.nowayback.user.domain.vo;

public enum UserStatus {
    PENDING {
        @Override
        public boolean canTransitionTo(UserStatus newStatus) {
            return newStatus == ACTIVE || newStatus == DEACTIVATED;
        }
    },
    ACTIVE {
        @Override
        public boolean canTransitionTo(UserStatus newStatus) {
            return newStatus == SUSPENDED || newStatus == DEACTIVATED;
        }
    },
    SUSPENDED {
        @Override
        public boolean canTransitionTo(UserStatus newStatus) {
            return newStatus == ACTIVE || newStatus == DEACTIVATED;
        }
    },
    DEACTIVATED {
        @Override
        public boolean canTransitionTo(UserStatus newStatus) {
            return newStatus == ACTIVE;
        }
    };

    public abstract boolean canTransitionTo(UserStatus newStatus);
}

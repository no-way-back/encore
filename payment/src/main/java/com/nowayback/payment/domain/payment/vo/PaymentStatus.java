package com.nowayback.payment.domain.payment.vo;

public enum PaymentStatus {

    PENDING {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == COMPLETED || newStatus == FAILED;
        }
    },
    COMPLETED {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == REFUNDED;
        }
    },
    FAILED {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return false;
        }
    },
    REFUNDED {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return false;
        }
    }
    ;

    public abstract boolean canTransitionTo(PaymentStatus newStatus);
}

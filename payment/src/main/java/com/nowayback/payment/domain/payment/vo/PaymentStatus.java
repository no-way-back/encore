package com.nowayback.payment.domain.payment.vo;

public enum PaymentStatus {

    PENDING {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == COMPLETED;
        }
    },
    COMPLETED {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == REFUNDED;
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

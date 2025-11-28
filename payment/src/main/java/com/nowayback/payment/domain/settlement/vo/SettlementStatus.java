package com.nowayback.payment.domain.settlement.vo;

public enum SettlementStatus {
    PROCESSING {
        @Override
        public boolean canTransitionTo(SettlementStatus newStatus) {
            return newStatus == COMPLETED || newStatus == FAILED;
        }
    },
    COMPLETED {
        @Override
        public boolean canTransitionTo(SettlementStatus newStatus) {
            return false;
        }
    },
    FAILED {
        @Override
        public boolean canTransitionTo(SettlementStatus newStatus) {
            return false;
        }
    },
    ;

    public abstract boolean canTransitionTo(SettlementStatus newStatus);
}

package com.nowayback.payment.domain.payment.vo;

import com.nowayback.payment.domain.exception.PaymentDomainErrorCode;
import com.nowayback.payment.domain.exception.PaymentDomainException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundAccountInfo {

    private String refundAccountBank;
    private String refundAccountNumber;
    private String refundAccountHolderName;

    private RefundAccountInfo(String refundAccountBank, String refundAccountNumber, String refundAccountHolderName) {
        this.refundAccountBank = refundAccountBank;
        this.refundAccountNumber = refundAccountNumber;
        this.refundAccountHolderName = refundAccountHolderName;
    }

    public static RefundAccountInfo of(String refundAccountBank, String refundAccountNumber, String refundAccountHolderName) {
        validateRefundAccountInfo(refundAccountBank, refundAccountNumber, refundAccountHolderName);
        return new RefundAccountInfo(refundAccountBank, refundAccountNumber, refundAccountHolderName);
    }

    private static void validateRefundAccountInfo(String refundAccountBank, String refundAccountNumber, String refundAccountHolderName) {
        validateRefundAccountBank(refundAccountBank);
        validateRefundAccountNumber(refundAccountNumber);
        validateRefundAccountHolderName(refundAccountHolderName);
    }

    private static void validateRefundAccountBank(String refundAccountBank) {
        if (refundAccountBank == null || refundAccountBank.trim().isEmpty()) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_REFUND_ACCOUNT_BANK_VALUE);
        }
    }

    private static void validateRefundAccountNumber(String refundAccountNumber) {
        if (refundAccountNumber == null || refundAccountNumber.trim().isEmpty()) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_REFUND_ACCOUNT_NUMBER_VALUE);
        }
    }

    private static void validateRefundAccountHolderName(String refundAccountHolderName) {
        if (refundAccountHolderName == null || refundAccountHolderName.trim().isEmpty()) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_REFUND_ACCOUNT_HOLDER_NAME_VALUE);
        }
    }
}

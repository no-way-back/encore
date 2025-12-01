package com.nowayback.payment.domain.settlement.vo;

import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountInfo {

    private String AccountBank;
    private String AccountNumber;
    private String AccountHolderName;

    private AccountInfo(String accountBank, String accountNumber, String accountHolderName) {
        AccountBank = accountBank;
        AccountNumber = accountNumber;
        AccountHolderName = accountHolderName;
    }

    public static AccountInfo of(String accountBank, String accountNumber, String accountHolderName) {
        validateAccountInfo(accountBank, accountNumber, accountHolderName);
        return new AccountInfo(accountBank, accountNumber, accountHolderName);
    }

    private static void validateAccountInfo(String accountBank, String accountNumber, String accountHolderName) {
        validateAccountBank(accountBank);
        validateAccountNumber(accountNumber);
        validateAccountHolderName(accountHolderName);
    }

    private static void validateAccountBank(String accountBank) {
        if (accountBank == null || accountBank.trim().isEmpty()) {
            throw new PaymentException(PaymentErrorCode.NULL_ACCOUNT_BANK_VALUE);
        }
    }

    private static void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new PaymentException(PaymentErrorCode.NULL_ACCOUNT_NUMBER_VALUE);
        }
    }

    private static void validateAccountHolderName(String accountHolderName) {
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new PaymentException(PaymentErrorCode.NULL_ACCOUNT_HOLDER_NAME_VALUE);
        }
    }
}

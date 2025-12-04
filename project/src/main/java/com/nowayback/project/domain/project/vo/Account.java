package com.nowayback.project.domain.project.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Account {
    private String accountBank;
    private String accountNumber;
    private String accountHolderName;

    public Account() {}

    private Account(String accountBank, String accountNumber, String accountHolderName) {
        this.accountBank = accountBank;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
    }

    public static Account create(
        String accountBank,
        String accountNumber,
        String accountHolderName
    ) {
        return new Account(accountBank, accountNumber, accountHolderName);
    }
}

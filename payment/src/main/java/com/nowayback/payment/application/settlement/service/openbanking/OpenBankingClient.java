package com.nowayback.payment.application.settlement.service.openbanking;

public interface OpenBankingClient {

    String transfer(String accountBank, String accountNumber, String accountHolderName, Long amount);
}

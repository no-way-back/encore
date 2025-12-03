package com.nowayback.payment.infrastructure.settlement.external.openbanking;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "kftc-open-banking",
        url = "${payment.open-banking.base-url}"
)
public interface OpenBankingFeignClient {

    @GetMapping("/transfer")
    String transfer(String accountBank, String accountNumber, String accountHolderName, Long amount);
}

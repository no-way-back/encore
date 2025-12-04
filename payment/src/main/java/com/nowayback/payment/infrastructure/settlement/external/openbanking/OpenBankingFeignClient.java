package com.nowayback.payment.infrastructure.settlement.external.openbanking;

import com.nowayback.payment.infrastructure.settlement.external.openbanking.dto.OpenBankingTransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "kftc-open-banking",
        url = "${payment.open-banking.base-url}"
)
public interface OpenBankingFeignClient {

    @PostMapping("/transfer")
    String transfer(OpenBankingTransferRequest request);
}

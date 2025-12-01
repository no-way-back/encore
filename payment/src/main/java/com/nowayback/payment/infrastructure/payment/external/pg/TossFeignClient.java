package com.nowayback.payment.infrastructure.payment.external.pg;

import com.nowayback.payment.infrastructure.payment.external.pg.dto.request.PgConfirmRequest;
import com.nowayback.payment.infrastructure.payment.external.pg.dto.request.PgRefundRequest;
import com.nowayback.payment.infrastructure.payment.external.pg.dto.response.PgConfirmResponse;
import com.nowayback.payment.infrastructure.payment.external.pg.dto.response.PgRefundResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "toss-payments",
        url = "${payment.toss.base-url}",
        configuration = TossFeignConfig.class
)
public interface TossFeignClient {

    @PostMapping("/payments/confirm")
    PgConfirmResponse confirmPayment(PgConfirmRequest request);

    @PostMapping("/payments/{paymentKey}/cancel")
    PgRefundResponse cancelPayment(
            @PathVariable("paymentKey") String paymentKey,
            @RequestBody PgRefundRequest request
    );
}

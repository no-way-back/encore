package com.nowayback.payment.presentation.payment;

import com.nowayback.payment.application.payment.PaymentStatusLogService;
import com.nowayback.payment.application.payment.dto.result.PaymentStatusLogResult;
import com.nowayback.payment.presentation.auth.role.RequiredRole;
import com.nowayback.payment.presentation.dto.response.PageResponse;
import com.nowayback.payment.presentation.payment.dto.response.PaymentStatusLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments/status-logs")
@RequiredArgsConstructor
public class PaymentStatusLogController {

    private final PaymentStatusLogService paymentStatusLogService;

    @GetMapping("{paymentId}")
    @RequiredRole({"MASTER", "ADMIN"})
    public ResponseEntity<PageResponse<PaymentStatusLogResponse>> getPaymentStatusLogs(
            @PathVariable("paymentId") UUID paymentId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Page<PaymentStatusLogResult> results = paymentStatusLogService.getPaymentStatusLogs(paymentId, page, size);

        return ResponseEntity.ok(PageResponse.from(results.map(PaymentStatusLogResponse::from)));
    }
}

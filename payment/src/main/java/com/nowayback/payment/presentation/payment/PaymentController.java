package com.nowayback.payment.presentation.payment;

import com.nowayback.payment.application.payment.PaymentService;
import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.infrastructure.auth.user.AuthUser;
import com.nowayback.payment.infrastructure.auth.user.CurrentUser;
import com.nowayback.payment.presentation.dto.response.PageResponse;
import com.nowayback.payment.presentation.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerDoc {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<PageResponse<PaymentResponse>> getPayments(
            @CurrentUser AuthUser authUser,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Page<PaymentResult> results = paymentService.getPayments(userId, projectId, page, size, authUser.userId(), authUser.role());

        return ResponseEntity.ok(PageResponse.from(results.map(PaymentResponse::from)));
    }
}

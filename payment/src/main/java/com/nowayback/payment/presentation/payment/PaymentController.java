package com.nowayback.payment.presentation.payment;

import com.nowayback.payment.application.payment.PaymentService;
import com.nowayback.payment.application.payment.dto.command.ConfirmPaymentCommand;
import com.nowayback.payment.application.payment.dto.command.RefundPaymentCommand;
import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.infrastructure.auth.user.AuthUser;
import com.nowayback.payment.infrastructure.auth.user.CurrentUser;
import com.nowayback.payment.presentation.dto.response.PageResponse;
import com.nowayback.payment.presentation.payment.dto.request.ConfirmPaymentRequest;
import com.nowayback.payment.presentation.payment.dto.request.RefundPaymentRequest;
import com.nowayback.payment.presentation.payment.dto.response.ConfirmPaymentResponse;
import com.nowayback.payment.presentation.payment.dto.response.PaymentResponse;
import com.nowayback.payment.presentation.payment.dto.response.RefundPaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/payments")
@RequiredArgsConstructor
public class PaymentController {

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


    @PostMapping("/confirm")
    public ResponseEntity<ConfirmPaymentResponse> confirmPayment(
            @RequestHeader(name = "X-User-Id") UUID userId,
            @Valid @RequestBody ConfirmPaymentRequest request
    ) {
        ConfirmPaymentCommand command = ConfirmPaymentCommand.of(
                userId,
                request.fundingId(),
                request.projectId(),
                request.amount(),
                request.pgMethod(),
                request.pgPaymentKey(),
                request.pgOrderId()
        );

        ConfirmPaymentResponse response = ConfirmPaymentResponse.from(paymentService.confirmPayment(command));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/refund")
    public ResponseEntity<RefundPaymentResponse> refundPayment(
            @Valid @RequestBody RefundPaymentRequest request
    ) {
        RefundPaymentCommand command = RefundPaymentCommand.of(
                request.paymentId(),
                request.reason(),
                request.refundAccountBank(),
                request.refundAccountNumber(),
                request.refundAccountHolderName()
        );

        RefundPaymentResponse response = RefundPaymentResponse.from(paymentService.refundPayment(command));

        return ResponseEntity.ok(response);
    }
}

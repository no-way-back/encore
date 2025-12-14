package com.nowayback.payment.infrastructure.payment.external.pg;

import com.nowayback.payment.application.payment.service.pg.PaymentGatewayClient;
import com.nowayback.payment.application.payment.service.pg.dto.PgConfirmResult;
import com.nowayback.payment.application.payment.service.pg.dto.PgRefundResult;
import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import com.nowayback.payment.domain.payment.vo.Money;
import com.nowayback.payment.domain.payment.vo.PgInfo;
import com.nowayback.payment.domain.payment.vo.RefundAccountInfo;
import com.nowayback.payment.infrastructure.payment.external.pg.dto.request.PgConfirmRequest;
import com.nowayback.payment.infrastructure.payment.external.pg.dto.request.PgRefundRequest;
import com.nowayback.payment.infrastructure.payment.external.pg.dto.response.PgConfirmResponse;
import com.nowayback.payment.infrastructure.payment.external.pg.dto.response.PgRefundResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentGatewayClient implements PaymentGatewayClient {

    private final TossFeignClient tossFeignClient;

    @Override
    public PgConfirmResult confirmPayment(PgInfo pgInfo, Money amount) {
        log.info("[External PG Toss] 결제 승인 - key: {}, orderId: {}, amount: {}",
                pgInfo.getPgPaymentKey(), pgInfo.getPgOrderId(), amount.getAmount());

        try {
            PgConfirmRequest request = new PgConfirmRequest(
                    pgInfo.getPgPaymentKey(),
                    pgInfo.getPgOrderId(),
                    amount.getAmount()
            );

            /*
            PgConfirmResponse response = tossFeignClient.confirmPayment(request);

            log.info("[External PG Toss] 결제 승인 성공 - status: {}", response.status());

            return new PgConfirmResult(
                    response.paymentKey(),
                    response.orderId(),
                    response.totalAmount(),
                    response.approvedAt().toLocalDateTime(),
                    response.status()
            );
             */

            return new PgConfirmResult(
                    pgInfo.getPgPaymentKey(),
                    pgInfo.getPgOrderId(),
                    amount.getAmount(),
                    LocalDateTime.now(),
                    "DONE"
            );
        } catch (FeignException e) {
            log.error("[External PG Toss] 결제 승인 실패 - {}", e.getMessage());
            throw new PaymentException(PaymentErrorCode.PG_CONFIRMATION_FAILED);
        }
    }

    @Override
    public PgRefundResult refundPayment(String paymentKey, String cancelReason, RefundAccountInfo refundAccountInfo) {
        log.info("[External PG Toss] 환불 처리 - key: {}, reason: {}",
                paymentKey, cancelReason);

        try {
            PgRefundRequest request = new PgRefundRequest(
                    cancelReason,
                    PgRefundRequest.RefundAccount.from(refundAccountInfo)
            );

            /*
            PgRefundResponse response = tossFeignClient.cancelPayment(paymentKey, request);

            log.info("[External PG Toss] 환불 처리 성공 - status: {}", response.status());

            PgRefundResponse.CancelInfo lastCancel = response.cancels().getLast();

            return new PgRefundResult(
                    response.paymentKey(),
                    response.orderId(),
                    lastCancel.cancelReason(),
                    lastCancel.canceledAt().toLocalDateTime(),
                    lastCancel.cancelAmount(),
                    lastCancel.cancelStatus()
            );
             */

            return new PgRefundResult(
                    paymentKey,
                    "orderId-placeholder",
                    cancelReason,
                    LocalDateTime.now(),
                    0L,
                    "CANCELED"
            );
        } catch (FeignException e) {
            log.error("[External PG Toss] 환불 처리 실패 - {}", e.getMessage());
            throw new PaymentException(PaymentErrorCode.PG_REFUND_FAILED);
        }
    }
}

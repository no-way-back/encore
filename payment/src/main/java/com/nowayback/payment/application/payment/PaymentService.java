package com.nowayback.payment.application.payment;

import com.nowayback.payment.application.payment.dto.command.ConfirmPaymentCommand;
import com.nowayback.payment.application.payment.dto.command.RefundPaymentCommand;
import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.application.payment.service.pg.PaymentGatewayClient;
import com.nowayback.payment.application.payment.service.pg.dto.PgConfirmResult;
import com.nowayback.payment.application.payment.service.pg.dto.PgRefundResult;
import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.repository.PaymentRepository;
import com.nowayback.payment.domain.payment.vo.FundingId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayClient paymentGatewayClient;

    @Transactional
    public PaymentResult confirmPayment(ConfirmPaymentCommand command) {
        PgConfirmResult pgResponse = paymentGatewayClient.confirmPayment(
                command.pgInfo(),
                command.amount()
        );

        Payment payment = Payment.create(
                command.userId(),
                command.fundingId(),
                command.amount(),
                command.pgInfo()
        );

        payment.complete(pgResponse.approvedAt());
        paymentRepository.save(payment);

        return PaymentResult.from(payment);
    }

    @Transactional
    public PaymentResult refundPayment(RefundPaymentCommand command) {
        Payment payment = getPaymentByFundingId(command.fundingId());

        PgRefundResult pgResponse = paymentGatewayClient.refundPayment(
                payment.getPgInfo().getPgPaymentKey(),
                command.cancelReason(),
                command.refundAccountInfo()
        );

        payment.refund(command.refundAccountInfo());

        return PaymentResult.from(payment);
    }

    private Payment getPaymentByFundingId(FundingId fundingId) {
        return paymentRepository.findByFundingId(fundingId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
    }
}

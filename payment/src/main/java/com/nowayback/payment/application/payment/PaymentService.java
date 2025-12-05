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
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import com.nowayback.payment.domain.payment.vo.Money;
import com.nowayback.payment.domain.payment.vo.ProjectId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayClient paymentGatewayClient;

    private final PaymentStatusLogService paymentStatusLogService;

    private static final int MAX_PAGE_SIZE = 50;

    @Transactional(readOnly = true)
    public Page<PaymentResult> getPayments(UUID userId, UUID projectId, int page, int size, UUID requesterId, String role) {
        Pageable pageable = getPageable(page, size);

        if (role.equals("USER")) {
            if (userId == null) userId = requesterId;
            else validateSelf(userId, requesterId);
        }

        return paymentRepository.searchPayments(userId, projectId, pageable)
                .map(PaymentResult::from);
    }

    @Transactional
    public PaymentResult confirmPayment(ConfirmPaymentCommand command) {
        PgConfirmResult pgResponse = paymentGatewayClient.confirmPayment(
                command.pgInfo(),
                command.amount()
        );

        Payment payment = Payment.create(
                command.userId(),
                command.fundingId(),
                command.projectId(),
                command.amount(),
                command.pgInfo()
        );

        PaymentStatus previous = payment.getStatus();
        payment.complete(pgResponse.approvedAt());

        paymentRepository.save(payment);
        savePaymentStatusLog(payment, previous, null, Money.of(pgResponse.totalAmount()));

        return PaymentResult.from(payment);
    }

    @Transactional
    public PaymentResult refundPayment(RefundPaymentCommand command) {
        Payment payment = getPaymentById(command.paymentId());

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED);
        }

        PgRefundResult pgResult = paymentGatewayClient.refundPayment(
                payment.getPgInfo().getPgPaymentKey(),
                command.cancelReason(),
                command.refundAccountInfo()
        );

        PaymentStatus previous = payment.getStatus();
        payment.refund(command.refundAccountInfo(), command.cancelReason(), pgResult.canceledAt());

        savePaymentStatusLog(payment, previous, null, Money.of(pgResult.cancelAmount()));

        return PaymentResult.from(payment);
    }

    public Long getTotalAmountByProjectId(UUID projectId) {
        return paymentRepository.sumAmountByProjectId(ProjectId.of(projectId));
    }

    private Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
    }

    private void savePaymentStatusLog(Payment payment, PaymentStatus prevStatus, String reason, Money amount) {
        paymentStatusLogService.savePaymentStatusLog(
                payment.getId(),
                prevStatus,
                payment.getStatus(),
                reason,
                amount
        );
    }

    private void validateSelf(UUID userId, UUID requesterId) {
        if (!userId.equals(requesterId)) {
            throw new PaymentException(PaymentErrorCode.FORBIDDEN_PAYMENT_SELF_ACCESS);
        }
    }

    private Pageable getPageable(int page, int size) {
        int safeSize = Math.min(size, MAX_PAGE_SIZE);
        return PageRequest.of(page, safeSize);
    }
}

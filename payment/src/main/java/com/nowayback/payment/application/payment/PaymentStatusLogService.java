package com.nowayback.payment.application.payment;

import com.nowayback.payment.application.payment.dto.result.PaymentStatusLogResult;
import com.nowayback.payment.domain.payment.entity.PaymentStatusLog;
import com.nowayback.payment.domain.payment.repository.PaymentStatusLogRepository;
import com.nowayback.payment.domain.payment.vo.Money;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentStatusLogService {

    private final PaymentStatusLogRepository paymentStatusLogRepository;

    private static final int MAX_PAGE_SIZE = 50;

    public void savePaymentStatusLog(UUID paymentId, PaymentStatus previousStatus, PaymentStatus currStatus, String reason, Money amount) {
        PaymentStatusLog paymentStatusLog = PaymentStatusLog.create(paymentId, previousStatus, currStatus, reason, amount);
        paymentStatusLogRepository.save(paymentStatusLog);
    }

    @Transactional(readOnly = true)
    public Page<PaymentStatusLogResult> getPaymentStatusLogs(UUID paymentId, int page, int size) {
        Pageable pageable = getPageable(page, size);

        return paymentStatusLogRepository.findAllByPaymentId(paymentId, pageable)
                .map(PaymentStatusLogResult::from);
    }

    private Pageable getPageable(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        int safeSize = Math.min(size, MAX_PAGE_SIZE);

        return PageRequest.of(page, safeSize, sort);
    }
}

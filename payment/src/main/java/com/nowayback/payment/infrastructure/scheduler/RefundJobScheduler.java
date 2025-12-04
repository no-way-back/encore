package com.nowayback.payment.infrastructure.scheduler;

import com.nowayback.payment.application.payment.PaymentService;
import com.nowayback.payment.application.payment.dto.command.RefundPaymentCommand;
import com.nowayback.payment.domain.jobs.entity.RefundJob;
import com.nowayback.payment.domain.jobs.repository.RefundJobRepository;
import com.nowayback.payment.domain.jobs.vo.RefundJobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundJobScheduler {

    private final RefundJobRepository refundJobRepository;
    private final PaymentService paymentService;

    @Scheduled(fixedDelay = 10000)
    public void processRefundJobs() {
        log.info("[Refund Job Scheduler] 환불 작업 스케줄러 시작");
        List<RefundJob> jobs = refundJobRepository.findTop100ByStatusOrderByCreatedAt(RefundJobStatus.PENDING);

        for (RefundJob job : jobs) {
            try {
                job.markInProgress();
                refundJobRepository.save(job);

                RefundPaymentCommand command = RefundPaymentCommand.of(
                        job.getPaymentId(),
                        job.getReason(),
                        null, null, null
                );

                paymentService.refundPayment(command);

                job.markCompleted();
            } catch (Exception e) {
                log.error("[Refund Job Scheduler] 환불 처리 실패 - Job ID: {}, Error: {}", job.getId(), e.getMessage());
                job.markFailed();
            }

            refundJobRepository.save(job);
        }
    }
}

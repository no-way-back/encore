package com.nowayback.payment.application.payment;

import com.nowayback.payment.application.payment.dto.result.PaymentStatusLogResult;
import com.nowayback.payment.domain.payment.repository.PaymentStatusLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import static com.nowayback.payment.fixture.PaymentStatusLogFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("결제 상태 기록 서비스")
@ExtendWith(MockitoExtension.class)
class PaymentStatusLogServiceTest {

    @Mock
    private PaymentStatusLogRepository paymentStatusLogRepository;

    @InjectMocks
    private PaymentStatusLogService paymentStatusLogService;

    @Nested
    @DisplayName("결제 상태 기록 저장")
    class SavePaymentStatusLog {

        @Test
        @DisplayName("유효한 정보로 결제 상태 기록을 저장하면 저장에 성공한다.")
        void savePaymentStatusLog_whenValid_thenSuccess() {
            /* given */
            /* when */
            paymentStatusLogService.savePaymentStatusLog(
                    PAYMENT_ID,
                    PREV_STATUS,
                    CURR_STATUS,
                    REASON,
                    AMOUNT
            );

            /* then */
            verify(paymentStatusLogRepository, times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("결제 상태 기록 조회")
    class GetPaymentStatusLogs {

        @Test
        @DisplayName("유효한 결제 ID로 결제 상태 기록을 조회하면 기록들을 반환한다.")
        void getPaymentStatusLogs_whenValidPaymentId_thenReturnLogs() {
            /* given */
            when(paymentStatusLogRepository.findAllByPaymentId(PAYMENT_ID, PAGEABLE))
                    .thenReturn(PAYMENT_STATUS_LOGS_PAGE);

            /* when */
            Page<PaymentStatusLogResult> result = paymentStatusLogService.getPaymentStatusLogs(PAYMENT_ID, PAGE, SIZE);

            /* then */
            assertThat(result.getContent()).hasSize(PAYMENT_STATUS_LOGS.size());
            verify(paymentStatusLogRepository, times(1)).findAllByPaymentId(PAYMENT_ID, PAGEABLE);
        }
    }
}
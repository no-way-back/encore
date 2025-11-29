package com.nowayback.payment.application.payment;

import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.application.payment.service.pg.PaymentGatewayClient;
import com.nowayback.payment.domain.payment.repository.PaymentRepository;
import com.nowayback.payment.domain.payment.vo.Money;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import com.nowayback.payment.domain.payment.vo.PgInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.nowayback.payment.fixture.PaymentFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("결제 서비스")
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGatewayClient paymentGatewayClient;

    @InjectMocks
    private PaymentService paymentService;

    @Nested
    @DisplayName("결제 승인")
    class Confirm {

        @Test
        @DisplayName("유효한 정보로 결제 승인을 하면 결제 승인이 요청되고 결제 정보가 저장된다.")
        void confirm_whenValid_thenCreatePayment() {
            /* given */
            when(paymentGatewayClient.confirmPayment(any(PgInfo.class), any(Money.class)))
                    .thenReturn(PG_CONFIRM_RESULT);

            /* when */
            PaymentResult result = paymentService.confirmPayment(CONFIRM_PAYMENT_COMMAND);

            /* then */
            assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETED);

            verify(paymentGatewayClient, times(1)).confirmPayment(any(PgInfo.class), any(Money.class));
            verify(paymentRepository, times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("결제 환불")
    class Refund {

        @Test
        @DisplayName("유효한 정보로 결제 환불을 하면 결제 환불이 요청되고 결제 정보가 수정된다.")
        void refund_whenValid_thenRefundPayment() {
            /* given */
            when(paymentRepository.findByFundingId(any()))
                    .thenReturn(Optional.of(createPaymentWithStatus(PaymentStatus.COMPLETED)));
            when(paymentGatewayClient.refundPayment(anyString(), any(Money.class), any()))
                    .thenReturn(PG_REFUND_RESULT);

            /* when */
            PaymentResult result = paymentService.refundPayment(REFUND_PAYMENT_COMMAND);

            /* then */
            assertThat(result.status()).isEqualTo(PaymentStatus.REFUNDED);

            verify(paymentGatewayClient, times(1)).refundPayment(anyString(), any(Money.class), any());
        }
    }
}
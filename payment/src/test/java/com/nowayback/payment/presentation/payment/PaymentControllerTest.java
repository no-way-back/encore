package com.nowayback.payment.presentation.payment;

import com.nowayback.payment.application.payment.PaymentService;
import com.nowayback.payment.application.payment.dto.command.ConfirmPaymentCommand;
import com.nowayback.payment.application.payment.dto.command.RefundPaymentCommand;
import com.nowayback.payment.presentation.ControllerTest;
import com.nowayback.payment.presentation.payment.dto.request.ConfirmPaymentRequest;
import com.nowayback.payment.presentation.payment.dto.request.RefundPaymentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.nowayback.payment.fixture.PaymentFixture.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("결제 컨트롤러")
@WebMvcTest(PaymentController.class)
class PaymentControllerTest extends ControllerTest {

    @MockitoBean
    private PaymentService paymentService;

    private static final String BASE_URL = "/payments";

    @Nested
    @DisplayName("결제 승인")
    class Confirm {

        @Test
        @DisplayName("유효한 요청이 들어오면 결제 승인에 성공한다.")
        void confirm_validRequest_success() throws Exception {
            /* given */
            ConfirmPaymentRequest request = VALID_CONFIRM_PAYMENT_REQUEST;

            given(paymentService.confirmPayment(any(ConfirmPaymentCommand.class)))
                    .willReturn(PAYMENT_RESULT_COMPLETED);

            /* when */
            /* then */
            performWithAuth(post(BASE_URL + "/confirm")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("pgPaymentKey").value(PAYMENT_RESULT_COMPLETED.pgPaymentKey()));
        }

        @Test
        @DisplayName("유효하지 않은 요청이 들어오면 결제 승인에 실패한다.")
        void confirm_invalidRequest_fail() throws Exception {
            /* given */
            ConfirmPaymentRequest request = INVALID_CONFIRM_PAYMENT_REQUEST;

            /* when */
            /* then */
            performWithAuth(post(BASE_URL + "/confirm")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("인증되지 않은 요청이 들어오면 결제 승인에 실패한다.")
        void confirm_unauthenticatedRequest_fail() throws Exception {
            /* given */
            ConfirmPaymentRequest request = VALID_CONFIRM_PAYMENT_REQUEST;

            /* when */
            /* then */
            mockMvc.perform(post(BASE_URL + "/confirm")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("결제 환불")
    class Refund {

        @Test
        @DisplayName("유효한 요청이 들어오면 결제 환불에 성공한다.")
        void refund_validRequest_success() throws Exception {
            /* given */
            RefundPaymentRequest request = VALID_REFUND_PAYMENT_REQUEST;

            given(paymentService.refundPayment(any(RefundPaymentCommand.class)))
                    .willReturn(PAYMENT_RESULT_REFUNDED);

            /* when */
            /* then */
            performWithAuth(post(BASE_URL + "/refund")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("pgPaymentKey").value(PAYMENT_RESULT_REFUNDED.pgPaymentKey()));
        }


        @Test
        @DisplayName("유효하지 않은 요청이 들어오면 결제 환불에 실패한다.")
        void refund_invalidRequest_fail() throws Exception {
            /* given */
            RefundPaymentRequest request = INVALID_REFUND_PAYMENT_REQUEST;

            /* when */
            /* then */
            performWithAuth(post(BASE_URL + "/refund")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
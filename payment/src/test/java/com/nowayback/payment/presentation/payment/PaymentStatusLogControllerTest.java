package com.nowayback.payment.presentation.payment;

import com.nowayback.payment.application.payment.PaymentStatusLogService;
import com.nowayback.payment.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.nowayback.payment.fixture.PaymentStatusLogFixture.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("결제 상태 기록 컨트롤러")
@WebMvcTest(PaymentStatusLogController.class)
class PaymentStatusLogControllerTest extends ControllerTest {

    @MockitoBean
    private PaymentStatusLogService logService;

    private static final String BASE_URL = "/payments/status-logs";

    @Nested
    @DisplayName("결제 상태 기록 조회")
    class GetPaymentStatusLogs {

        @ParameterizedTest
        @DisplayName("유효한 요청이 들어오면 결제 상태 기록 페이지를 반환한다.")
        @ValueSource(strings = {"MASTER", "ADMIN"})
        void getPaymentStatusLogs_whenValidRequest_thenReturnPaymentStatusLogsPage(String role) throws Exception {
            /* given */
            given(logService.getPaymentStatusLogs(any(), anyInt(), anyInt()))
                    .willReturn(PAYMENT_STATUS_LOG_RESULT_PAGE);

            /* when */
            /* then */
            performWithAuth(get(BASE_URL + "/{paymentId}", PAYMENT_ID), role)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(PAYMENT_STATUS_LOG_RESULT_PAGE.getContent().size()))
                    .andExpect(jsonPath("$.totalPages").value(PAYMENT_STATUS_LOG_RESULT_PAGE.getTotalPages()))
                    .andExpect(jsonPath("$.totalElements").value(PAYMENT_STATUS_LOG_RESULT_PAGE.getTotalElements()))
                    .andExpect(jsonPath("$.number").value(PAYMENT_STATUS_LOG_RESULT_PAGE.getNumber()))
                    .andExpect(jsonPath("$.size").value(PAYMENT_STATUS_LOG_RESULT_PAGE.getSize()))
                    .andExpect(jsonPath("$.first").value(PAYMENT_STATUS_LOG_RESULT_PAGE.isFirst()))
                    .andExpect(jsonPath("$.last").value(PAYMENT_STATUS_LOG_RESULT_PAGE.isLast()));
        }

        @Test
        @DisplayName("인증되지 않은 요청이 들어오면 응답코드 401을 반환한다.")
        void getPaymentStatusLogs_unauthorizedRequest_unauthorized() throws Exception {
            /* given */
            /* when */
            /* then */
            mockMvc.perform(get(BASE_URL + "/{paymentId}", PAYMENT_ID))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest
        @DisplayName("권한이 없는 요청이 들어오면 응답코드 403을 반환한다.")
        @ValueSource(strings = {"USER"})
        void getPaymentStatusLogs_invalidRole_forbidden(String role) throws Exception {
            /* given */
            /* when */
            /* then */
            performWithAuth(get(BASE_URL + "/{paymentId}", PAYMENT_ID), role)
                    .andExpect(status().isForbidden());
        }
    }
}
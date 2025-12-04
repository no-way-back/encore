package com.nowayback.payment.presentation.settlement;

import com.nowayback.payment.application.settlement.SettlementStatusLogService;
import com.nowayback.payment.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.nowayback.payment.fixture.SettlementStatusLogFixture.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("정산 상태 기록 컨트롤러")
@WebMvcTest(SettlementStatusLogController.class)
class SettlementStatusLogControllerTest extends ControllerTest {

    @MockitoBean
    private SettlementStatusLogService logService;

    private static final String BASE_URL = "/settlements/status-logs";

    @Nested
    @DisplayName("정산 상태 기록 조회")
    class GetSettlementStatusLogs {

        @ParameterizedTest(name = "{0} 권한으로 요청")
        @DisplayName("유효한 요청이 들어오면 정산 상태 기록 페이지를 반환한다.")
        @ValueSource(strings = {"MASTER", "ADMIN"})
        void getSettlementStatusLogs_whenValidRequest_thenReturnSettlementStatusLogsPage(String role) throws Exception {
            /* given */
            given(logService.getSettlementStatusLogs(any(), anyInt(), anyInt()))
                    .willReturn(SETTLEMENT_STATUS_LOG_RESULT_PAGE);

            /* when */
            /* then */
            performWithAuth(get(BASE_URL)
                            .param("settlementId", SETTLEMENT_ID.toString()), role)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(SETTLEMENT_STATUS_LOG_RESULT_PAGE.getContent().size()))
                    .andExpect(jsonPath("$.totalPages").value(SETTLEMENT_STATUS_LOG_RESULT_PAGE.getTotalPages()))
                    .andExpect(jsonPath("$.totalElements").value(SETTLEMENT_STATUS_LOG_RESULT_PAGE.getTotalElements()))
                    .andExpect(jsonPath("$.number").value(SETTLEMENT_STATUS_LOG_RESULT_PAGE.getNumber()))
                    .andExpect(jsonPath("$.size").value(SETTLEMENT_STATUS_LOG_RESULT_PAGE.getSize()))
                    .andExpect(jsonPath("$.first").value(SETTLEMENT_STATUS_LOG_RESULT_PAGE.isFirst()))
                    .andExpect(jsonPath("$.last").value(SETTLEMENT_STATUS_LOG_RESULT_PAGE.isLast()));
        }

        @Test
        @DisplayName("인증되지 않은 요청이 들어오면 응답코드 401을 반환한다.")
        void getSettlementStatusLogs_unauthorizedRequest_unauthorized() throws Exception {
            /* given */
            /* when */
            /* then */
            perform(get(BASE_URL).param("settlementId", SETTLEMENT_ID.toString()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "{0} 권한으로 요청")
        @DisplayName("권한이 없는 요청이 들어오면 응답코드 403을 반환한다.")
        @ValueSource(strings = {"USER"})
        void getSettlementStatusLogs_forbiddenRequest_forbidden(String role) throws Exception {
            /* given */
            /* when */
            /* then */
            performWithAuth(get(BASE_URL).param("settlementId", SETTLEMENT_ID.toString()), role)
                    .andExpect(status().isForbidden());
        }
    }
}
package com.nowayback.payment.presentation.settlement;

import com.nowayback.payment.application.settlement.SettlementService;
import com.nowayback.payment.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static com.nowayback.payment.fixture.SettlementFixture.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("정산 컨트롤러")
@WebMvcTest(SettlementController.class)
class SettlementControllerTest extends ControllerTest {

    @MockitoBean
    private SettlementService settlementService;

    private static final String BASE_URL = "/settlements";

    @Nested
    @DisplayName("정산 조회")
    class GetSettlement {

        @Test
        @DisplayName("유효한 요청이 들어오면 정산 조회에 성공한다.")
        void getSettlement_validRequest_success() throws Exception {
            /* given */

            given(settlementService.getSettlement(any(UUID.class)))
                    .willReturn(SETTLEMENT_RESULT_COMPLETED);

            /* when */
            /* then */
            performWithAuth(get(BASE_URL + "/{projectId}", PROJECT_UUID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("projectId").value(PROJECT_UUID.toString()))
                    .andExpect(jsonPath("status").value(SETTLEMENT_RESULT_COMPLETED.status().toString()));
        }
    }
}
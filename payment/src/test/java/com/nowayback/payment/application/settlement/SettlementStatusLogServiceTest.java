package com.nowayback.payment.application.settlement;

import com.nowayback.payment.application.settlement.dto.result.SettlementStatusLogResult;
import com.nowayback.payment.domain.settlement.repository.SettlementStatusLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import static com.nowayback.payment.fixture.SettlementStatusLogFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("정산 상태 기록 서비스")
@ExtendWith(MockitoExtension.class)
class SettlementStatusLogServiceTest {

    @Mock
    private SettlementStatusLogRepository settlementStatusLogRepository;

    @InjectMocks
    private SettlementStatusLogService settlementStatusLogService;

    @Nested
    @DisplayName("정산 상태 기록 저장")
    class SaveSettlementStatusLog {

        @Test
        @DisplayName("유효한 정보로 정산 상태 기록을 저장하면 저장에 성공한다.")
        void saveSettlementStatusLog_whenValid_thenSuccess() {
            /* given */
            /* when */
            settlementStatusLogService.saveSettlementStatusLog(
                    SETTLEMENT_ID,
                    PREV_STATUS,
                    CURR_STATUS,
                    REASON,
                    AMOUNT
            );

            /* then */
            verify(settlementStatusLogRepository, times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("정산 상태 기록 목록 조회")
    class GetSettlementStatusLogs {

        @Test
        @DisplayName("유효한 정산 ID로 정산 상태 기록을 조회하면 기록들을 반환한다.")
        void getSettlementStatusLogs_whenValidSettlementId_thenReturnLogs() {
            /* given */
            when(settlementStatusLogRepository.findAllBySettlementId(SETTLEMENT_ID, PAGEABLE))
                    .thenReturn(SETTLEMENT_STATUS_LOGS_PAGE);

            /* when */
            Page<SettlementStatusLogResult> results = settlementStatusLogService.getSettlementStatusLogs(SETTLEMENT_ID, PAGE, SIZE);

            /* then */
            assertThat(results.getContent()).hasSize(SETTLEMENT_STATUS_LOGS.size());
            verify(settlementStatusLogRepository, times(1)).findAllBySettlementId(SETTLEMENT_ID, PAGEABLE);
        }
    }
}
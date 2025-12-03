package com.nowayback.payment.application.settlement;

import com.nowayback.payment.application.payment.PaymentService;
import com.nowayback.payment.application.settlement.dto.result.SettlementResult;
import com.nowayback.payment.application.settlement.service.openbanking.OpenBankingClient;
import com.nowayback.payment.application.settlement.service.project.ProjectClient;
import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.settlement.entity.Settlement;
import com.nowayback.payment.domain.settlement.repository.SettlementRepository;
import com.nowayback.payment.domain.settlement.vo.SettlementFeePolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.nowayback.payment.fixture.SettlementFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("정산 서비스")
@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private SettlementFeePolicy settlementFeePolicy;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private OpenBankingClient openBankingClient;

    @InjectMocks
    private SettlementService settlementService;

    @Nested
    @DisplayName("정산 처리")
    class ProcessSettlement {

        @Test
        @DisplayName("유효한 프로젝트 ID로 정산을 처리하면 정산이 생성되고 저장된다.")
        void processSettlement_whenValidProjectId_thenCreateAndSaveSettlement() {
            /* given */
            Settlement settlement = createSettlement();

            when(settlementRepository.existsByProjectId(PROJECT_ID))
                    .thenReturn(false);
            when(paymentService.getTotalAmountByProjectId(PROJECT_UUID))
                    .thenReturn(TOTAL_AMOUNT_VALUE);
            when(projectClient.getProjectAccountInfo(PROJECT_UUID))
                    .thenReturn(PROJECT_ACCOUNT_RESULT);
            when(settlementRepository.save(any()))
                    .thenReturn(settlement);
            when(openBankingClient.transfer(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyLong()
            )).thenReturn(TRANSACTION_ID);

            /* when */
            SettlementResult result = settlementService.processSettlement(PROJECT_UUID);

            /* then */
            assertThat(result.projectId()).isEqualTo(settlement.getProjectId().getId());
            verify(settlementRepository, times(1)).save(any(Settlement.class));
        }

        @Test
        @DisplayName("이미 정산이 처리된 프로젝트 ID로 정산을 처리하면 예외가 발생한다.")
        void processSettlement_whenDuplicateProjectId_thenThrowException() {
            /* given */
            when(settlementRepository.existsByProjectId(PROJECT_ID))
                    .thenReturn(true);

            /* when */
            /* then */
            assertThatThrownBy(() -> settlementService.processSettlement(PROJECT_UUID))
                    .isInstanceOf(Exception.class)
                    .hasMessage(PaymentErrorCode.DUPLICATE_SETTLEMENT.getMessage());
        }
    }

    @Nested
    @DisplayName("정산 조회")
    class GetSettlement {

        @Test
        @DisplayName("존재하는 프로젝트 ID로 정산을 조회하면 정산 정보를 반환한다.")
        void getSettlement_whenExistingProjectId_thenReturnSettlement() {
            /* given */
            Settlement settlement = createSettlement();

            when(settlementRepository.findByProjectId(PROJECT_ID))
                    .thenReturn(java.util.Optional.of(settlement));

            /* when */
            SettlementResult result = settlementService.getSettlement(PROJECT_UUID);

            /* then */
            assertThat(result.projectId()).isEqualTo(settlement.getProjectId().getId());
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트 ID로 정산을 조회하면 예외가 발생한다.")
        void getSettlement_whenNonExistingProjectId_thenThrowException() {
            /* given */
            when(settlementRepository.findByProjectId(PROJECT_ID))
                    .thenReturn(java.util.Optional.empty());

            /* when */
            /* then */
            assertThatThrownBy(() -> settlementService.getSettlement(PROJECT_UUID))
                    .isInstanceOf(Exception.class)
                    .hasMessage(PaymentErrorCode.SETTLEMENT_NOT_FOUND.getMessage());
        }
    }
}
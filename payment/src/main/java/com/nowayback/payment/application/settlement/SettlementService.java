package com.nowayback.payment.application.settlement;

import com.nowayback.payment.application.payment.PaymentService;
import com.nowayback.payment.application.settlement.dto.result.SettlementResult;
import com.nowayback.payment.application.settlement.service.openbanking.OpenBankingClient;
import com.nowayback.payment.application.settlement.service.project.ProjectClient;
import com.nowayback.payment.application.settlement.service.project.dto.ProjectAccountResult;
import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import com.nowayback.payment.domain.settlement.entity.Settlement;
import com.nowayback.payment.domain.settlement.repository.SettlementRepository;
import com.nowayback.payment.domain.settlement.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final PaymentService paymentService;
    private final SettlementFeePolicy settlementFeePolicy;

    private final ProjectClient projectClient;
    private final OpenBankingClient openBankingClient;

    private final SettlementStatusLogService settlementStatusLogService;

    @Transactional
    public SettlementResult processSettlement(UUID projectId) {
        validateDuplicateSettlement(ProjectId.of(projectId));

        Long totalAmount = paymentService.getTotalAmountByProjectId(projectId);
        ProjectAccountResult account = projectClient.getProjectAccountInfo(projectId);

        Settlement settlement = Settlement.create(
                ProjectId.of(projectId),
                Money.of(totalAmount),
                AccountInfo.of(
                        account.accountBank(),
                        account.accountNumber(),
                        account.accountHolderName()
                ),
                settlementFeePolicy
        );
        Settlement savedSettlement = settlementRepository.save(settlement);

        String transactionId = openBankingClient.transfer(
                account.accountBank(),
                account.accountNumber(),
                account.accountHolderName(),
                savedSettlement.getNetAmount().getAmount()
        );

        SettlementStatus previous = savedSettlement.getStatus();
        settlement.complete();

        saveSettlementStatusLog(savedSettlement, previous, null, savedSettlement.getNetAmount());

        return SettlementResult.from(savedSettlement);
    }

    @Transactional(readOnly = true)
    public SettlementResult getSettlement(UUID projectId) {
        Settlement settlement = getSettlementByProjectId(projectId);
        return SettlementResult.from(settlement);
    }

    private Settlement getSettlementByProjectId(UUID projectId) {
        return settlementRepository.findByProjectId(ProjectId.of(projectId))
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.SETTLEMENT_NOT_FOUND));
    }

    private void validateDuplicateSettlement(ProjectId projectId) {
        if (settlementRepository.existsByProjectId(projectId)) {
            throw new PaymentException(PaymentErrorCode.DUPLICATE_SETTLEMENT);
        }
    }

    private void saveSettlementStatusLog(Settlement settlement, SettlementStatus previousStatus, String reason, Money amount) {
        settlementStatusLogService.saveSettlementStatusLog(
                settlement.getId(),
                previousStatus,
                settlement.getStatus(),
                reason,
                amount
        );
    }
}

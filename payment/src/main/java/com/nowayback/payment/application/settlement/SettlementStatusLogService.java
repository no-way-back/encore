package com.nowayback.payment.application.settlement;

import com.nowayback.payment.application.settlement.dto.result.SettlementStatusLogResult;
import com.nowayback.payment.domain.settlement.entity.SettlementStatusLog;
import com.nowayback.payment.domain.settlement.repository.SettlementStatusLogRepository;
import com.nowayback.payment.domain.settlement.vo.Money;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementStatusLogService {

    private final SettlementStatusLogRepository settlementStatusLogRepository;

    private static final int MAX_PAGE_SIZE = 50;

    @Transactional
    public void saveSettlementStatusLog(UUID settlementId, SettlementStatus previousStatus, SettlementStatus currStatus, String reason, Money amount) {
        SettlementStatusLog settlementStatusLog = SettlementStatusLog.create(settlementId, previousStatus, currStatus, reason, amount);
        settlementStatusLogRepository.save(settlementStatusLog);
    }

    @Transactional(readOnly = true)
    public Page<SettlementStatusLogResult> getSettlementStatusLogs(UUID settlementId, int page, int size) {
        Pageable pageable = getPageable(page, size);

        return settlementStatusLogRepository.findAllBySettlementId(settlementId, pageable)
                .map(SettlementStatusLogResult::from);
    }

    private Pageable getPageable(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        int safeSize = Math.min(size, MAX_PAGE_SIZE);

        return PageRequest.of(page, safeSize, sort);
    }
}

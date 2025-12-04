package com.nowayback.payment.fixture;

import com.nowayback.payment.domain.settlement.entity.SettlementStatusLog;
import com.nowayback.payment.domain.settlement.vo.Money;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.UUID;

public class SettlementStatusLogFixture {
    public static final UUID SETTLEMENT_STATUS_LOG_ID = UUID.randomUUID();

    public static final UUID SETTLEMENT_ID = UUID.randomUUID();
    public static final SettlementStatus PREV_STATUS = SettlementStatus.PROCESSING;
    public static final SettlementStatus CURR_STATUS = SettlementStatus.COMPLETED;
    public static final String REASON = "Settlement completed successfully";

    public static final long AMOUNT_VALUE = 10_000L;
    public static final Money AMOUNT = Money.of(AMOUNT_VALUE);

    public static final int PAGE = 0;
    public static final int SIZE = 10;
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "createdAt");
    public static final Pageable PAGEABLE = PageRequest.of(PAGE, SIZE, SORT);

    /* Settlement Status Log Entity */

    public static SettlementStatusLog createSettlementStatusLog() {
        return SettlementStatusLog.create(
                SETTLEMENT_ID,
                PREV_STATUS,
                CURR_STATUS,
                REASON,
                AMOUNT
        );
    }

    public static final List<SettlementStatusLog> PAYMENT_STATUS_LOGS = List.of(
            createSettlementStatusLog(),
            createSettlementStatusLog()
    );

    public static final Page<SettlementStatusLog> PAYMENT_STATUS_LOGS_PAGE = new PageImpl<>(PAYMENT_STATUS_LOGS, PAGEABLE, PAYMENT_STATUS_LOGS.size());

    /* Settlement Status Log Result */

//    public static final Page<SettlementStatusLogResult> SETTLEMENT_STATUS_LOG_RESULT_PAGE = PAYMENT_STATUS_LOGS_PAGE.map(SettlementStatusLogResult::from);
}

package com.nowayback.payment.fixture;

import com.nowayback.payment.application.settlement.dto.result.SettlementResult;
import com.nowayback.payment.application.settlement.service.project.dto.ProjectAccountResult;
import com.nowayback.payment.domain.settlement.entity.Settlement;
import com.nowayback.payment.domain.settlement.vo.AccountInfo;
import com.nowayback.payment.domain.settlement.vo.Money;
import com.nowayback.payment.domain.settlement.vo.ProjectId;
import com.nowayback.payment.domain.settlement.vo.SettlementFeePolicy;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;

import java.lang.reflect.Field;
import java.util.UUID;

public class SettlementFixture {

    public static final UUID PROJECT_UUID = UUID.randomUUID();
    public static final ProjectId PROJECT_ID = ProjectId.of(PROJECT_UUID);

    public static final long TOTAL_AMOUNT_VALUE = 100_000L;
    public static final Money TOTAL_AMOUNT = Money.of(TOTAL_AMOUNT_VALUE);

    public static final long SERVICE_FEE_VALUE = 5_000L;
    public static final Money SERVICE_FEE = Money.of(SERVICE_FEE_VALUE);

    public static final long PG_FEE_VALUE = 3_000L;
    public static final Money PG_FEE = Money.of(PG_FEE_VALUE);

    public static final long NET_AMOUNT_VALUE = TOTAL_AMOUNT_VALUE - SERVICE_FEE_VALUE - PG_FEE_VALUE;
    public static final Money NET_AMOUNT = Money.of(NET_AMOUNT_VALUE);

    public static final String ACCOUNT_BANK = "KAKAOBANK";
    public static final String ACCOUNT_NUMBER = "123-456-7890";
    public static final String ACCOUNT_HOLDER_NAME = "홍길동";
    public static final AccountInfo ACCOUNT_INFO = AccountInfo.of(ACCOUNT_BANK, ACCOUNT_NUMBER, ACCOUNT_HOLDER_NAME);

    public static final SettlementFeePolicy FEE_POLICY = new SettlementFeePolicy();

    /* Settlement Entity */

    public static Settlement createSettlement() {
        return Settlement.create(PROJECT_ID, TOTAL_AMOUNT, ACCOUNT_INFO, FEE_POLICY);
    }

    public static Settlement createSettlementWithStatus(SettlementStatus status) {
        Settlement settlement = createSettlement();
        setPrivateField(settlement, "status", status);
        return settlement;
    }

    /* Settlement Result */

    public static final SettlementResult SETTLEMENT_RESULT_COMPLETED = SettlementResult.from(createSettlementWithStatus(SettlementStatus.COMPLETED));

    /**
     * External Client Fixture
     */

    /* Project Client */
    public static ProjectAccountResult PROJECT_ACCOUNT_RESULT =
            new ProjectAccountResult(PROJECT_UUID, ACCOUNT_BANK, ACCOUNT_NUMBER, ACCOUNT_HOLDER_NAME);

    /* Open Banking Client */
    public static String TRANSACTION_ID = "TXN1234567890";

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

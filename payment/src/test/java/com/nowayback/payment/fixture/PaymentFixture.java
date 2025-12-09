package com.nowayback.payment.fixture;

import com.nowayback.payment.application.payment.dto.command.ConfirmPaymentCommand;
import com.nowayback.payment.application.payment.dto.command.RefundPaymentCommand;
import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.application.payment.service.pg.dto.PgConfirmResult;
import com.nowayback.payment.application.payment.service.pg.dto.PgRefundResult;
import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.vo.*;
import com.nowayback.payment.presentation.payment.dto.request.ConfirmPaymentRequest;
import com.nowayback.payment.presentation.payment.dto.request.RefundPaymentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PaymentFixture {

    public static final UUID PAYMENT_UUID = UUID.randomUUID();

    public static final UUID USER_UUID = UUID.randomUUID();
    public static final UserId USER_ID = UserId.of(USER_UUID);

    public static final UUID FUNDING_UUID = UUID.randomUUID();
    public static final FundingId FUNDING_ID = FundingId.of(FUNDING_UUID);

    public static final UUID PROJECT_UUID = UUID.randomUUID();
    public static final ProjectId PROJECT_ID = ProjectId.of(PROJECT_UUID);

    public static final long AMOUNT_VALUE = 20_000L;
    public static final Money AMOUNT = Money.of(AMOUNT_VALUE);

    public static final PaymentStatus STATUS = PaymentStatus.PENDING;

    public static final String PG_METHOD = "CARD";
    public static final String PG_PAYMENT_KEY = "pg_payment_key_123";
    public static final String PG_ORDER_ID = "pg_order_id_123";
    public static final PgInfo PG_INFO = PgInfo.of(PG_METHOD, PG_PAYMENT_KEY, PG_ORDER_ID);

    public static final String REFUND_ACCOUNT_BANK = "KAKAOBANK";
    public static final String REFUND_ACCOUNT_NUMBER = "123-456-7890";
    public static final String REFUND_ACCOUNT_HOLDER_NAME = "홍길동";
    public static final RefundAccountInfo REFUND_ACCOUNT_INFO = RefundAccountInfo.of(REFUND_ACCOUNT_BANK, REFUND_ACCOUNT_NUMBER, REFUND_ACCOUNT_HOLDER_NAME);

    public static final String REFUND_REASON = "단순 변심";

    public static final LocalDateTime APPROVED_AT = LocalDateTime.now();
    public static final LocalDateTime FAILED_AT = LocalDateTime.now();
    public static final LocalDateTime REFUNDED_AT = LocalDateTime.now();

    public static final int PAGE = 0;
    public static final int SIZE = 10;
    public static final Pageable PAGEABLE = PageRequest.of(PAGE, SIZE);

    /* payment entity */

    public static Payment createPayment() {
        return Payment.create(
                USER_ID,
                FUNDING_ID,
                PROJECT_ID,
                AMOUNT
        );
    }

    public static Payment createPaymentWithStatus(PaymentStatus status) {
        Payment payment = createPayment();
        setPrivateField(payment, "status", status);
        return payment;
    }

    public static Payment createPayment(UserId userId, ProjectId projectId) {
        return Payment.create(
                userId,
                FUNDING_ID,
                projectId,
                AMOUNT
        );
    }

    private static final List<Payment> PAYMENT_LIST = List.of(
            createPayment(),
            createPayment()
    );

    public static final Page<Payment> PAYMENT_PAGE = new PageImpl<>(PAYMENT_LIST, PAGEABLE, PAYMENT_LIST.size());

    /* payment command */

    public static final ConfirmPaymentCommand CONFIRM_PAYMENT_COMMAND = ConfirmPaymentCommand.of(
            USER_UUID,
            FUNDING_UUID,
            PROJECT_UUID,
            AMOUNT_VALUE,
            PG_METHOD,
            PG_PAYMENT_KEY,
            PG_ORDER_ID
    );

    public static final RefundPaymentCommand REFUND_PAYMENT_COMMAND = RefundPaymentCommand.of(
            PAYMENT_UUID,
            REFUND_REASON,
            REFUND_ACCOUNT_BANK,
            REFUND_ACCOUNT_NUMBER,
            REFUND_ACCOUNT_HOLDER_NAME
    );

    /* payment result */

    public static final PaymentResult PAYMENT_RESULT_PENDING = PaymentResult.from(createPayment());
    public static final PaymentResult PAYMENT_RESULT_COMPLETED = PaymentResult.from(createPaymentWithStatus(PaymentStatus.COMPLETED));
    public static final PaymentResult PAYMENT_RESULT_REFUNDED = PaymentResult.from(createPaymentWithStatus(PaymentStatus.REFUNDED));

    public static final Page<PaymentResult> PAYMENT_RESULT_PAGE = PAYMENT_PAGE.map(PaymentResult::from);

    /* payment request */

    public static final ConfirmPaymentRequest VALID_CONFIRM_PAYMENT_REQUEST = new ConfirmPaymentRequest(
            FUNDING_UUID,
            PROJECT_UUID,
            AMOUNT_VALUE,
            PG_METHOD,
            PG_PAYMENT_KEY,
            PG_ORDER_ID
    );

    public static final ConfirmPaymentRequest INVALID_CONFIRM_PAYMENT_REQUEST = new ConfirmPaymentRequest(
            null,
            null,
            null,
            "",
            "",
            ""
    );

    public static final RefundPaymentRequest VALID_REFUND_PAYMENT_REQUEST = new RefundPaymentRequest(
            FUNDING_UUID,
            REFUND_REASON,
            REFUND_ACCOUNT_BANK,
            REFUND_ACCOUNT_NUMBER,
            REFUND_ACCOUNT_HOLDER_NAME
    );

    public static final RefundPaymentRequest INVALID_REFUND_PAYMENT_REQUEST = new RefundPaymentRequest(
            null,
            "",
            "",
            "",
            ""
    );

    /**
     * External Client Fixture
     */

    /* Payment Gateway Client */
    public static final PgConfirmResult PG_CONFIRM_RESULT = new PgConfirmResult(
            PG_PAYMENT_KEY,
            PG_ORDER_ID,
            AMOUNT_VALUE,
            APPROVED_AT,
            "APPROVED"
    );

    private static final Long REFUND_AMOUNT = 20_000L;
    private static final String REFUND_STATUS = "CANCELED";

    public static final PgRefundResult PG_REFUND_RESULT = new PgRefundResult(
            PG_PAYMENT_KEY,
            PG_ORDER_ID,
            REFUND_REASON,
            REFUNDED_AT,
            REFUND_AMOUNT,
            REFUND_STATUS
    );

    /* private methods */

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

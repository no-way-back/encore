package com.nowayback.payment.application.payment.service.pg;

import com.nowayback.payment.application.payment.service.pg.dto.PgConfirmResult;
import com.nowayback.payment.application.payment.service.pg.dto.PgRefundResult;
import com.nowayback.payment.domain.payment.vo.Money;
import com.nowayback.payment.domain.payment.vo.PgInfo;
import com.nowayback.payment.domain.payment.vo.RefundAccountInfo;

public interface PaymentGatewayClient {

    PgConfirmResult confirmPayment(PgInfo pgInfo, Money amount);
    PgRefundResult refundPayment(String cancelReason, Money cancelAmount, RefundAccountInfo refundAccountInfo);
}
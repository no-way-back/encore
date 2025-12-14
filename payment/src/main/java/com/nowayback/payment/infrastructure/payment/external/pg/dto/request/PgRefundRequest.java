package com.nowayback.payment.infrastructure.payment.external.pg.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nowayback.payment.domain.payment.vo.RefundAccountInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PgRefundRequest (
        String cancelReason,
        RefundAccount refundReceiveAccount
) {

    public record RefundAccount (
            String bank,
            String accountNumber,
            String holderName
    ) {

        public static RefundAccount from(RefundAccountInfo info) {
            if (info == null) return null;

            return new RefundAccount(
                    info.getRefundAccountBank(),
                    info.getRefundAccountNumber(),
                    info.getRefundAccountHolderName()
            );
        }
    }
}

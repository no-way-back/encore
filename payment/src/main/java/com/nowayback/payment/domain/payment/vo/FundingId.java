package com.nowayback.payment.domain.payment.vo;

import com.nowayback.payment.domain.exception.PaymentDomainErrorCode;
import com.nowayback.payment.domain.exception.PaymentDomainException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundingId {

    private UUID id;

    private FundingId(UUID id) {
        this.id = id;
    }

    public static FundingId of(UUID id) {
        if (id == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_FUNDING_ID_VALUE);
        }
        return new FundingId(id);
    }
}

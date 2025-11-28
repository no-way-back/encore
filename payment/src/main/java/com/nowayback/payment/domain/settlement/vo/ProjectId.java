package com.nowayback.payment.domain.settlement.vo;

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
public class ProjectId {

    private UUID id;

    private ProjectId(UUID id) {
        this.id = id;
    }

    public static ProjectId of(UUID id) {
        if (id == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_PROJECT_ID_VALUE);
        }
        return new ProjectId(id);
    }
}

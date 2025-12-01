package com.nowayback.payment.domain.payment.vo;

import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
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

    public ProjectId(UUID id) {
        this.id = id;
    }

    public static ProjectId of(UUID id) {
        if (id == null) {
            throw new PaymentException(PaymentErrorCode.NULL_PAYMENT_PROJECT_ID_VALUE);
        }
        return new ProjectId(id);
    }
}

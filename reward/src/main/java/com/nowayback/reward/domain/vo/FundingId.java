package com.nowayback.reward.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundingId implements Serializable {

    @Column(name = "funding_id", nullable = false)
    private UUID value;

    private FundingId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("펀딩 ID는 null일 수 없습니다.");
        }
        this.value = value;
    }

    public static FundingId of(UUID value) {
        return new FundingId(value);
    }
}
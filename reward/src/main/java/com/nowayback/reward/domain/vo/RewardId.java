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
public class RewardId implements Serializable {

    @Column(name = "reward_id", nullable = false)
    private UUID value;

    private RewardId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("리워드 ID는 null일 수 없습니다.");
        }
        this.value = value;
    }

    public static RewardId of(UUID value) {
        return new RewardId(value);
    }
}
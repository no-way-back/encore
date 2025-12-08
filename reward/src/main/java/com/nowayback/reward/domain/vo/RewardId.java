package com.nowayback.reward.domain.vo;

import com.nowayback.reward.domain.exception.RewardException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

import static com.nowayback.reward.domain.exception.RewardErrorCode.REWARD_ID_IS_NULL;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RewardId implements Serializable {

    @Column(name = "reward_id", nullable = false)
    private UUID id;

    private RewardId(UUID id) {
        if (id == null) {
            throw new RewardException(REWARD_ID_IS_NULL);
        }
        this.id = id;
    }

    public static RewardId of(UUID id) {
        return new RewardId(id);
    }
}
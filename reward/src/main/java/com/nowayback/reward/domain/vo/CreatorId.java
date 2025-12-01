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
public class CreatorId implements Serializable {

    @Column(name = "creator_id", nullable = false)
    private UUID value;

    private CreatorId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("생성자 ID는 null일 수 없습니다.");
        }
        this.value = value;
    }

    public static CreatorId of(UUID value) {
        return new CreatorId(value);
    }
}
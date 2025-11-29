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
public class OptionId implements Serializable {

    @Column(name = "option_id")
    private UUID value;  // nullable

    private OptionId(UUID value) {
        this.value = value;
    }

    public static OptionId of(UUID value) {
        return new OptionId(value);
    }

    public static OptionId ofNullable(UUID value) {
        return value != null ? new OptionId(value) : null;
    }
}
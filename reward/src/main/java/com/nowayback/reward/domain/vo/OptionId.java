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
    private UUID id;  // nullable

    private OptionId(UUID id) {
        this.id = id;
    }

    public static OptionId of(UUID id) {
        return new OptionId(id);
    }

    public static OptionId ofNullable(UUID value) {
        return value != null ? new OptionId(value) : null;
    }
}
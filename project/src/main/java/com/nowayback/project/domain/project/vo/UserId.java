package com.nowayback.project.domain.project.vo;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserId {

    private UUID id;

    private UserId(UUID id) {
        this.id = id;
    }

    public static UserId create(UUID id) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }

        return new UserId(id);
    }
}

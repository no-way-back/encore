package com.nowayback.reward.domain.vo;

import com.nowayback.reward.domain.exception.RewardErrorCode;
import com.nowayback.reward.domain.exception.RewardException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

import static com.nowayback.reward.domain.exception.RewardErrorCode.*;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectId implements Serializable {

    @Column(name = "project_id", nullable = false)
    private UUID id;

    private ProjectId(UUID id) {
        if (id == null) {
            throw new RewardException(PROJECT_ID_IS_NULL);
        }
        this.id = id;
    }

    public static ProjectId of(UUID id) {
        return new ProjectId(id);
    }
}
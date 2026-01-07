package com.nowayback.project.domain.project.vo;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectDraftId {

    private UUID id;

    private ProjectDraftId(UUID id) {
        this.id = id;
    }

    public static ProjectDraftId create(UUID id) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }

        return new ProjectDraftId(id);
    }
}

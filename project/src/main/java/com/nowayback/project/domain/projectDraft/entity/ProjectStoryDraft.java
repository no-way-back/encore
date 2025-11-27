package com.nowayback.project.domain.projectDraft.entity;

import com.nowayback.project.domain.exception.ProjectDomainErrorCode;
import com.nowayback.project.domain.exception.ProjectDomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_project_story_draft")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectStoryDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String summary;
    private String category;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "content_json", columnDefinition = "TEXT")
    private String contentJson;


    public static ProjectStoryDraft create() {
        return new ProjectStoryDraft();
    }

    public boolean update(
        String title,
        String summary,
        String category,
        String thumbnailUrl,
        String contentJson
    ) {
        validateBasic(title, summary, category, thumbnailUrl, contentJson);

        this.title = title;
        this.summary = summary;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.contentJson = contentJson;

        return isCompleted();
    }

    private void validateBasic(
        String title,
        String summary,
        String category,
        String thumbnailUrl,
        String contentJson
    ) {
        if (title != null && title.isBlank()) {
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_STORY_TITLE);
        }
        if (summary != null && summary.isBlank()) {
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_STORY_SUMMARY);
        }
        if (category != null && category.isBlank()) {
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_STORY_CATEGORY);
        }
        if (thumbnailUrl != null && thumbnailUrl.isBlank()) {
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_STORY_THUMBNAIL);
        }
        if (contentJson != null && contentJson.isBlank()) {
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_STORY_CONTENT);
        }
    }

    public boolean isCompleted() {
        return title != null
            && summary != null
            && category != null
            && thumbnailUrl != null
            && contentJson != null;
    }

    public void validateForSubmission() {
        List<String> errors = new ArrayList<>();

        if (title == null || title.isBlank()) {
            errors.add(ProjectDomainErrorCode.INVALID_STORY_TITLE.getMessage());
        }
        if (summary == null || summary.isBlank()) {
            errors.add(ProjectDomainErrorCode.INVALID_STORY_SUMMARY.getMessage());
        }
        if (category == null || category.isBlank()) {
            errors.add(ProjectDomainErrorCode.INVALID_STORY_CATEGORY.getMessage());
        }
        if (thumbnailUrl == null || thumbnailUrl.isBlank()) {
            errors.add(ProjectDomainErrorCode.INVALID_STORY_THUMBNAIL.getMessage());
        }
        if (contentJson == null || contentJson.isBlank()) {
            errors.add(ProjectDomainErrorCode.INVALID_STORY_CONTENT.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_STORY_DRAFT_SUBMISSION);
        }
    }

}

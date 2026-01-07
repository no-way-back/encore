package com.nowayback.project.domain.project.entity;

import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.project.vo.Account;
import com.nowayback.project.domain.project.vo.Period;
import com.nowayback.project.domain.project.vo.ProjectDraftId;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import com.nowayback.project.domain.project.vo.UserId;
import com.nowayback.project.domain.shard.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_projects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id", updatable = false, nullable = false))
    private UserId userId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "project_draft_id", updatable = false, nullable = false))
    private ProjectDraftId projectDraftId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String summary;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "root_category_id", nullable = false)
    private UUID rootCategoryId;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "content_hml", columnDefinition = "TEXT", nullable = false)
    private String contentHtml;

    @Column(name = "goal_amount", nullable = false)
    private Long goalAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "start_date", column = @Column(name = "start_date", updatable = false, nullable = false)),
        @AttributeOverride(name = "end_date", column = @Column(name = "end_date", updatable = false, nullable = false))
    })
    private Period period;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @Column(name = "creation_failed_reason")
    private String creationFailedReason;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "accountBank", column = @Column(name = "account_bank")),
        @AttributeOverride(name = "accountNumber", column = @Column(name = "account_number")),
        @AttributeOverride(name = "accountHolderName", column = @Column(name = "account_holder_name")),
    })
    private Account account;


    private Project(
        UserId userId,
        ProjectDraftId projectDraftId,
        String title,
        String summary,
        UUID categoryId,
        UUID rootCategoryId,
        String thumbnailUrl,
        String contentHtml,
        Long goalAmount,
        Period period,
        Account account
    ) {
        validateRequired(userId, period, title, summary, categoryId, contentHtml, goalAmount);
        this.userId = userId;
        this.projectDraftId = projectDraftId;
        this.title = title;
        this.summary = summary;
        this.categoryId = categoryId;
        this.rootCategoryId = rootCategoryId;
        this.thumbnailUrl = thumbnailUrl;
        this.contentHtml = contentHtml;
        this.goalAmount = goalAmount;
        this.period = period;
        this.status = ProjectStatus.CREATE_PENDING;
        this.account = account;
    }

    public static Project create(
        UserId userId,
        ProjectDraftId projectDraftId,
        String title,
        String summary,
        UUID categoryId,
        UUID rootCategoryId,
        String thumbnailUrl,
        String contentHtml,
        Long goalAmount,
        Period period,
        Account account
    ) {
        return new Project(
            userId,
            projectDraftId,
            title,
            summary,
            categoryId,
            rootCategoryId,
            thumbnailUrl,
            contentHtml,
            goalAmount,
            period,
            account
        );
    }

    private void validateRequired(
        UserId userId,
        Period period,
        String title,
        String summary,
        UUID category,
        String contentHtml,
        Long goalAmount
    ) {
        if (userId == null) {
            throw new ProjectException(ProjectErrorCode.NULL_USER_ID);
        }

        if (period == null) {
            throw new ProjectException(ProjectErrorCode.INVALID_FUNDING_PERIOD);
        }

        if (title == null || title.isBlank()) {
            throw new ProjectException(ProjectErrorCode.NULL_TITLE);
        }
        if (summary == null || summary.isBlank()) {
            throw new ProjectException(ProjectErrorCode.NULL_SUMMARY);
        }
        if (category == null) {
            throw new ProjectException(
                ProjectErrorCode.NULL_CATEGORY);
        }
        if (contentHtml == null || contentHtml.isBlank()) {
            throw new ProjectException(
                ProjectErrorCode.NULL_CONTENT);
        }
        if (goalAmount == null || goalAmount <= 0) {
            throw new ProjectException(
                ProjectErrorCode.INVALID_GOAL_AMOUNT);
        }
    }

    public void startFunding() {
        if (this.status != ProjectStatus.UPCOMING) {
            throw new ProjectException(ProjectErrorCode.INVALID_STATUS_FOR_START);
        }
        this.status = ProjectStatus.LIVE;
    }

    public void endFunding(boolean success) {
        if (this.status != ProjectStatus.LIVE) {
            throw new ProjectException(ProjectErrorCode.INVALID_STATUS_FOR_END);
        }
        this.status = success ? ProjectStatus.SUCCESS : ProjectStatus.FAIL;
    }

    public void markAsUpcoming() {
        this.status = ProjectStatus.UPCOMING;
    }

    public void markAsCreationFailed(String reason) {
        this.status = ProjectStatus.CREATION_FAILED;
        this.creationFailedReason = reason;
    }
}

package com.nowayback.project.domain.project.entity;

import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.project.vo.Account;
import com.nowayback.project.domain.project.vo.ProjectStatus;
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
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.cfg.defs.UUIDDef;

@Getter
@Entity
@Table(name = "p_projects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID projectDraftId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private String category;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "content_hml", columnDefinition = "TEXT", nullable = false)
    private String contentHtml;

    @Column(name = "goal_amount", nullable = false)
    private Long goalAmount;

    @Column(name = "funding_start_date", nullable = false)
    private LocalDate fundingStartDate;

    @Column(name = "funding_end_date", nullable = false)
    private LocalDate fundingEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "accountBank", column = @Column(name = "account_bank")),
        @AttributeOverride(name = "accountNumber", column = @Column(name = "account_number")),
        @AttributeOverride(name = "accountHolderName", column = @Column(name = "account_holder_name")),
    })
    private Account account;

    private Project(
        UUID userId,
        UUID projectDraftId,
        String title,
        String summary,
        String category,
        String thumbnailUrl,
        String contentHtml,
        Long goalAmount,
        LocalDate fundingStartDate,
        LocalDate fundingEndDate,
        Account account
    ) {
        validateRequired(userId, title, summary, category, contentHtml, goalAmount, fundingStartDate, fundingEndDate);
        validateFundingPeriod(fundingStartDate, fundingEndDate);

        this.userId = userId;
        this.projectDraftId = projectDraftId;
        this.title = title;
        this.summary = summary;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.contentHtml = contentHtml;
        this.goalAmount = goalAmount;
        this.fundingStartDate = fundingStartDate;
        this.fundingEndDate = fundingEndDate;
        this.status = ProjectStatus.CREATE_PENDING;
        this.account = account;
    }

    public static Project create(
        UUID userId,
        UUID  projectDraftId,
        String title,
        String summary,
        String category,
        String thumbnailUrl,
        String contentHtml,
        Long goalAmount,
        LocalDate fundingStartDate,
        LocalDate fundingEndDate,
        Account account
    ) {
        return new Project(
            userId,
            projectDraftId,
            title,
            summary,
            category,
            thumbnailUrl,
            contentHtml,
            goalAmount,
            fundingStartDate,
            fundingEndDate,
            account
        );
    }

    private void validateRequired(
        UUID userId,
        String title,
        String summary,
        String category,
        String contentHtml,
        Long goalAmount,
        LocalDate fundingStartDate,
        LocalDate fundingEndDate
    ) {
        if (userId == null) throw new ProjectException(ProjectErrorCode.NULL_USER_ID);
        if (title == null || title.isBlank()) throw new ProjectException(ProjectErrorCode.NULL_TITLE);
        if (summary == null || summary.isBlank()) throw new ProjectException(ProjectErrorCode.NULL_SUMMARY);
        if (category == null || category.isBlank()) throw new ProjectException(
            ProjectErrorCode.NULL_CATEGORY);
        if (contentHtml == null || contentHtml.isBlank()) throw new ProjectException(
            ProjectErrorCode.NULL_CONTENT);
        if (goalAmount == null || goalAmount <= 0) throw new ProjectException(
            ProjectErrorCode.INVALID_GOAL_AMOUNT);
        if (fundingStartDate == null) throw new ProjectException(ProjectErrorCode.NULL_FUNDING_START);
        if (fundingEndDate == null) throw new ProjectException(ProjectErrorCode.NULL_FUNDING_END);
    }

    private void validateFundingPeriod(LocalDate start, LocalDate end) {
        if (!end.isAfter(start)) {
            throw new ProjectException(ProjectErrorCode.INVALID_FUNDING_PERIOD);
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
}

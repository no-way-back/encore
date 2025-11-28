package com.nowayback.project.domain.project.entity;

<<<<<<< HEAD
import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
=======
import com.nowayback.project.domain.exception.ProjectDomainErrorCode;
import com.nowayback.project.domain.exception.ProjectDomainException;
>>>>>>> develop
import com.nowayback.project.domain.project.vo.ProjectStatus;
import com.nowayback.project.domain.shard.BaseEntity;
import jakarta.persistence.Column;
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

@Getter
@Entity
<<<<<<< HEAD
@Table(name = "p_projects")
=======
@Table(name = "p_project")
>>>>>>> develop
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

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


    private Project(
        UUID userId,
        String title,
        String summary,
        String category,
        String thumbnailUrl,
        String contentHtml,
        Long goalAmount,
        LocalDate fundingStartDate,
        LocalDate fundingEndDate
    ) {
        validateRequired(userId, title, summary, category, contentHtml, goalAmount, fundingStartDate, fundingEndDate);
        validateFundingPeriod(fundingStartDate, fundingEndDate);

        this.userId = userId;
        this.title = title;
        this.summary = summary;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.contentHtml = contentHtml;
        this.goalAmount = goalAmount;
        this.fundingStartDate = fundingStartDate;
        this.fundingEndDate = fundingEndDate;

        this.status = ProjectStatus.CREATE_PENDING;
    }

    public static Project create(
        UUID userId,
        String title,
        String summary,
        String category,
        String thumbnailUrl,
        String contentHtml,
        Long goalAmount,
        LocalDate fundingStartDate,
        LocalDate fundingEndDate
    ) {
        return new Project(
            userId,
            title,
            summary,
            category,
            thumbnailUrl,
            contentHtml,
            goalAmount,
            fundingStartDate,
            fundingEndDate
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
<<<<<<< HEAD
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
=======
        if (userId == null) throw new ProjectDomainException(ProjectDomainErrorCode.NULL_USER_ID);
        if (title == null || title.isBlank()) throw new ProjectDomainException(ProjectDomainErrorCode.NULL_TITLE);
        if (summary == null || summary.isBlank()) throw new ProjectDomainException(ProjectDomainErrorCode.NULL_SUMMARY);
        if (category == null || category.isBlank()) throw new ProjectDomainException(ProjectDomainErrorCode.NULL_CATEGORY);
        if (contentHtml == null || contentHtml.isBlank()) throw new ProjectDomainException(ProjectDomainErrorCode.NULL_CONTENT);
        if (goalAmount == null || goalAmount <= 0) throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_GOAL_AMOUNT);
        if (fundingStartDate == null) throw new ProjectDomainException(ProjectDomainErrorCode.NULL_FUNDING_START);
        if (fundingEndDate == null) throw new ProjectDomainException(ProjectDomainErrorCode.NULL_FUNDING_END);
>>>>>>> develop
    }

    private void validateFundingPeriod(LocalDate start, LocalDate end) {
        if (!end.isAfter(start)) {
<<<<<<< HEAD
            throw new ProjectException(ProjectErrorCode.INVALID_FUNDING_PERIOD);
=======
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_FUNDING_PERIOD);
>>>>>>> develop
        }
    }

    public void startFunding() {
        if (this.status != ProjectStatus.UPCOMING) {
<<<<<<< HEAD
            throw new ProjectException(ProjectErrorCode.INVALID_STATUS_FOR_START);
=======
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_STATUS_FOR_START);
>>>>>>> develop
        }
        this.status = ProjectStatus.LIVE;
    }

    public void endFunding(boolean success) {
        if (this.status != ProjectStatus.LIVE) {
<<<<<<< HEAD
            throw new ProjectException(ProjectErrorCode.INVALID_STATUS_FOR_END);
=======
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_STATUS_FOR_END);
>>>>>>> develop
        }
        this.status = success ? ProjectStatus.SUCCESS : ProjectStatus.FAIL;
    }
}

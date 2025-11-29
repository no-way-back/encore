package com.nowayback.project.domain.projectDraft.entity;

import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.shard.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_project_funding_drafts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectFundingDraft extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "goal_amount")
    private Long goalAmount;

    @Column(name = "funding_start_date")
    private LocalDate fundingStartDate;

    @Column(name = "funding_end_date")
    private LocalDate fundingEndDate;


    public static ProjectFundingDraft create() {
        return new ProjectFundingDraft();
    }

    public boolean update(Long goalAmount, LocalDate fundingStartDate, LocalDate fundingEndDate) {
        validateGoalAmount(goalAmount);
        validateFundingPeriod(fundingStartDate, fundingEndDate);

        this.goalAmount = goalAmount;
        this.fundingStartDate = fundingStartDate;
        this.fundingEndDate = fundingEndDate;

        return isCompleted();
    }

    private void validateGoalAmount(Long goalAmount) {
        if (goalAmount != null && goalAmount <= 0) {
            throw new ProjectException(ProjectErrorCode.INVALID_GOAL_AMOUNT);
        }
    }

    private void validateFundingPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return;
        }

        if (startDate != null && startDate.isBefore(LocalDate.now())) {
            throw new ProjectException(ProjectErrorCode.INVALID_FUNDING_START_DATE);
        }

        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                throw new ProjectException(ProjectErrorCode.INVALID_FUNDING_PERIOD);
            }
            if (startDate.equals(endDate)) {
                throw new ProjectException(
                    ProjectErrorCode.INVALID_FUNDING_PERIOD_EQUAL);
            }
        }
    }

    public boolean isCompleted() {
        return goalAmount != null
            && fundingStartDate != null
            && fundingEndDate != null;
    }

    public void validateForSubmission() {
        List<String> errors = new ArrayList<>();

        if (goalAmount == null || goalAmount <= 0) {
            errors.add(ProjectErrorCode.INVALID_GOAL_AMOUNT.getMessage());
        }
        if (fundingStartDate == null || fundingStartDate.isBefore(LocalDate.now())) {
            errors.add(ProjectErrorCode.INVALID_FUNDING_START_DATE.getMessage());
        }
        if (fundingEndDate == null) {
            errors.add(ProjectErrorCode.NULL_FUNDING_END.getMessage());
        }
        if (fundingStartDate != null && fundingEndDate != null) {
            if (fundingEndDate.isBefore(fundingStartDate)) {
                errors.add(ProjectErrorCode.INVALID_FUNDING_PERIOD.getMessage());
            }
            if (fundingStartDate.equals(fundingEndDate)) {
                errors.add(ProjectErrorCode.INVALID_FUNDING_PERIOD_EQUAL.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            throw new ProjectException(
                ProjectErrorCode.INVALID_FUNDING_DRAFT_SUBMISSION);

        }
    }
}

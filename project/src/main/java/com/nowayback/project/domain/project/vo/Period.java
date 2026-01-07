package com.nowayback.project.domain.project.vo;

import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Period {

    private LocalDate startDate;
    private LocalDate endDate;

    private Period(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Period create(LocalDate startDate, LocalDate endDate) {
        validateNull(startDate, endDate);
        validateFundingPeriod(startDate, endDate);
        return new Period(startDate, endDate);
    }

    private static void validateNull(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new ProjectException(ProjectErrorCode.NULL_FUNDING_START);
        }

        if (endDate == null) {
            throw new ProjectException(ProjectErrorCode.NULL_FUNDING_END);
        }
    }

    private static void validateFundingPeriod(LocalDate startDate, LocalDate endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new ProjectException(ProjectErrorCode.INVALID_FUNDING_PERIOD);
        }
    }
}

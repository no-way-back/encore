package com.nowayback.project.presentation.projectdraft.dto.request;

import java.time.LocalDate;

public record SaveProjectFundingDraftRequest(
    Long goalAmount,
    LocalDate fundingStartDate,
    LocalDate fundingEndDate
) {

}

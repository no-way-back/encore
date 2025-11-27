package com.nowayback.project.presentation.projectdraft.request;

import java.time.LocalDate;

public record SaveProjectFundingDraftRequest(
    Long goalAmount,
    LocalDate fundingStartDate,
    LocalDate fundingEndDate
) {

}

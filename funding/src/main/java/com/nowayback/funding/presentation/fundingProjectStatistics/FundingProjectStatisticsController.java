package com.nowayback.funding.presentation.fundingProjectStatistics;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nowayback.funding.application.fundingProjectStatistics.dto.result.FundingProjectStatisticsResult;
import com.nowayback.funding.application.fundingProjectStatistics.service.FundingProjectStatisticsService;
import com.nowayback.funding.presentation.fundingProjectStatistics.dto.response.FundingProjectStatisticsResponse;

@RestController
@RequestMapping("/fundings")
public class FundingProjectStatisticsController implements FundingProjectStatisticsControllerDoc {

    private final FundingProjectStatisticsService fundingProjectStatisticsService;

    public FundingProjectStatisticsController(FundingProjectStatisticsService fundingProjectStatisticsService) {
        this.fundingProjectStatisticsService = fundingProjectStatisticsService;
    }

    @Override
    @GetMapping("/status?projectId={projectId}")
    public ResponseEntity<FundingProjectStatisticsResponse> getFundingProjectStatistics(
        @PathVariable("projectId") UUID projectId
    ) {
        FundingProjectStatisticsResult result = fundingProjectStatisticsService.getFundingProjectStatistics(projectId);

        FundingProjectStatisticsResponse response = FundingProjectStatisticsResponse.from(result);

        return ResponseEntity.ok(response);
    }
}

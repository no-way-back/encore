package com.nowayback.payment.presentation.settlement;

import com.nowayback.payment.application.settlement.SettlementService;
import com.nowayback.payment.application.settlement.dto.result.SettlementResult;
import com.nowayback.payment.presentation.settlement.dto.response.SettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/settlements")
public class InternalSettlementController {

    private final SettlementService settlementService;

    @PostMapping("/{projectId}")
    public ResponseEntity<SettlementResponse> processSettlement(
            @PathVariable("projectId") UUID projectId
    ) {
        SettlementResult result = settlementService.processSettlement(projectId);
        SettlementResponse response = SettlementResponse.from(result);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}

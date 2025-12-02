package com.nowayback.payment.presentation.settlement;

import com.nowayback.payment.application.settlement.SettlementService;
import com.nowayback.payment.application.settlement.dto.result.SettlementResult;
import com.nowayback.payment.presentation.settlement.dto.response.SettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/settlements")
@RequiredArgsConstructor
public class SettlementController {

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

    @GetMapping("/{projectId}")
    public ResponseEntity<SettlementResponse> getSettlement(
            @PathVariable("projectId") UUID projectId
    ) {
        SettlementResult result = settlementService.getSettlement(projectId);
        SettlementResponse response = SettlementResponse.from(result);

        return ResponseEntity.ok(response);
    }
}

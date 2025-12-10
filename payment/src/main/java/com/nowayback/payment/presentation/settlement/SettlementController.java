package com.nowayback.payment.presentation.settlement;

import com.nowayback.payment.application.settlement.SettlementService;
import com.nowayback.payment.application.settlement.dto.result.SettlementResult;
import com.nowayback.payment.presentation.settlement.dto.response.SettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settlements")
public class SettlementController implements SettlementControllerDoc {

    private final SettlementService settlementService;

    @GetMapping("/{projectId}")
    public ResponseEntity<SettlementResponse> getSettlement(
            @PathVariable("projectId") UUID projectId
    ) {
        SettlementResult result = settlementService.getSettlement(projectId);
        SettlementResponse response = SettlementResponse.from(result);

        return ResponseEntity.ok(response);
    }
}

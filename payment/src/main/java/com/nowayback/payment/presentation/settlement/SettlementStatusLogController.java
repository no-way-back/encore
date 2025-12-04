package com.nowayback.payment.presentation.settlement;

import com.nowayback.payment.application.settlement.SettlementStatusLogService;
import com.nowayback.payment.infrastructure.auth.role.RequiredRole;
import com.nowayback.payment.presentation.dto.response.PageResponse;
import com.nowayback.payment.presentation.settlement.dto.response.SettlementStatusLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/settlements/status-logs")
@RequiredArgsConstructor
public class SettlementStatusLogController {

    private final SettlementStatusLogService settlementStatusLogService;

    @GetMapping
    @RequiredRole({"MASTER", "ADMIN"})
    public ResponseEntity<PageResponse<SettlementStatusLogResponse>> getSettlementStatusLog(
            @RequestParam("settlementId")UUID settlementId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        var results = settlementStatusLogService.getSettlementStatusLogs(settlementId, page, size);

        return ResponseEntity.ok(PageResponse.from(results.map(SettlementStatusLogResponse::from)));
    }
}

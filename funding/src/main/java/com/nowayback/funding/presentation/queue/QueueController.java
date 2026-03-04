package com.nowayback.funding.presentation.queue;

import com.nowayback.funding.application.queue.FundingQueueService;
import com.nowayback.funding.application.queue.dto.command.EnqueueFundingCommand;
import com.nowayback.funding.application.queue.dto.result.EnqueueResult;
import com.nowayback.funding.application.queue.dto.result.QueuePositionResult;
import com.nowayback.funding.presentation.queue.dto.request.EnqueueRequest;
import com.nowayback.funding.presentation.queue.dto.response.EnqueueResponse;
import com.nowayback.funding.presentation.queue.dto.response.QueuePositionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 대기열 컨트롤러
 */
@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
@Slf4j
public class QueueController {

    private final FundingQueueService queueService;

    /**
     * 대기열 등록
     * POST /queue
     */
    @PostMapping
    public ResponseEntity<EnqueueResponse> enqueue(
            @RequestHeader(value = "X-User-Id") UUID userId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Validated @RequestBody EnqueueRequest request
    ) {
        log.info("대기열 등록 요청 - userId: {}, projectId: {}", userId, request.projectId());

        String finalIdempotencyKey = idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString();

        EnqueueFundingCommand command = request.toCommand(userId, finalIdempotencyKey);
        EnqueueResult result = queueService.enqueue(command);
        EnqueueResponse response = EnqueueResponse.from(result);

        return ResponseEntity.ok(response);
    }

    /**
     * 대기 순번 조회
     * GET /queue/position
     */
    @GetMapping("/position")
    public ResponseEntity<QueuePositionResponse> getPosition(
            @RequestHeader(value = "X-User-Id") UUID userId
    ) {
        log.debug("순번 조회 요청 - userId: {}", userId);

        QueuePositionResult result = queueService.getPosition(userId);
        QueuePositionResponse response = QueuePositionResponse.from(result);

        return ResponseEntity.ok(response);
    }

    /**
     * 대기열 제거 (테스트용)
     * DELETE /queue
     */
    @DeleteMapping
    public ResponseEntity<Void> remove(
            @RequestHeader(value = "X-User-Id") UUID userId
    ) {
        log.info("대기열 제거 요청 - userId: {}", userId);

        queueService.remove(userId);

        return ResponseEntity.noContent().build();
    }
}
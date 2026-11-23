package com.nowayback.reward.medium.application.inbox;

import com.nowayback.reward.application.inbox.InboxProcessor;
import com.nowayback.reward.application.inbox.repository.InboxRepository;
import com.nowayback.reward.domain.inbox.entity.Inbox;
import com.nowayback.reward.domain.inbox.vo.InboxStatus;
import com.nowayback.reward.domain.vo.EventType;
import com.nowayback.reward.medium.PostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 저장소를 mock하지 않고 실제 PostgreSQL에서 인박스 동작을 확인한다.
 * (mock 기반 단위 테스트로는 JPA merge 때문에 중복이 통과되는 문제를 잡을 수 없었다)
 */
class InboxProcessorIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private InboxProcessor inboxProcessor;

    @Autowired
    private InboxRepository inboxRepository;

    private Inbox find(UUID eventId) {
        return inboxRepository.findById(eventId).orElseThrow();
    }

    @Test
    @DisplayName("같은 이벤트가 두 번 와도 비즈니스 로직은 한 번만 실행된다")
    void duplicateEvent_processedOnce() {
        UUID eventId = UUID.randomUUID();
        AtomicInteger executed = new AtomicInteger();

        for (int i = 0; i < 2; i++) {
            inboxProcessor.processEvent(eventId, EventType.FUNDING_COMPLETED, UUID.randomUUID(),
                    "payload", p -> executed.incrementAndGet());
        }

        assertThat(executed.get()).isEqualTo(1);
        assertThat(find(eventId).getStatus()).isEqualTo(InboxStatus.PROCESSED);
    }

    @Test
    @DisplayName("처리에 실패하면 본 작업이 롤백돼도 실패 상태와 메시지가 남는다")
    void failure_recordedAfterRollback() {
        UUID eventId = UUID.randomUUID();

        assertThatThrownBy(() -> inboxProcessor.processEvent(eventId, EventType.FUNDING_COMPLETED,
                UUID.randomUUID(), "payload", p -> { throw new IllegalStateException("재고 복원 실패"); }))
                .isInstanceOf(IllegalStateException.class);

        Inbox inbox = find(eventId);
        assertThat(inbox.getStatus()).isEqualTo(InboxStatus.FAILED);
        assertThat(inbox.getErrorMessage()).isEqualTo("재고 복원 실패");
        assertThat(inbox.getRetryCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("실패한 이벤트가 재전송되면 다시 처리해 완료 상태가 된다")
    void failedEvent_succeedsOnRedelivery() {
        UUID eventId = UUID.randomUUID();
        AtomicInteger succeeded = new AtomicInteger();

        assertThatThrownBy(() -> inboxProcessor.processEvent(eventId, EventType.FUNDING_COMPLETED,
                UUID.randomUUID(), "payload", p -> { throw new IllegalStateException("일시적 실패"); }))
                .isInstanceOf(IllegalStateException.class);

        inboxProcessor.processEvent(eventId, EventType.FUNDING_COMPLETED, UUID.randomUUID(),
                "payload", p -> succeeded.incrementAndGet());

        assertThat(succeeded.get()).isEqualTo(1);
        assertThat(find(eventId).getStatus()).isEqualTo(InboxStatus.PROCESSED);
    }

    @Test
    @DisplayName("같은 이벤트가 동시에 들어와도 한 번만 처리된다")
    void concurrentDuplicate_processedOnce() throws Exception {
        UUID eventId = UUID.randomUUID();
        AtomicInteger executed = new AtomicInteger();
        int threads = 5;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    inboxProcessor.processEvent(eventId, EventType.FUNDING_COMPLETED, UUID.randomUUID(),
                            "payload", p -> {
                                executed.incrementAndGet();
                                sleep(200);
                            });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();
        assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
        pool.shutdown();

        assertThat(executed.get()).isEqualTo(1);
        assertThat(find(eventId).getStatus()).isEqualTo(InboxStatus.PROCESSED);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

package com.nowayback.reward.small.application.inbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.reward.application.inbox.InboxProcessor;
import com.nowayback.reward.application.inbox.InboxStatusUpdater;
import com.nowayback.reward.application.inbox.repository.InboxRepository;
import com.nowayback.reward.domain.inbox.entity.Inbox;
import com.nowayback.reward.domain.vo.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboxProcessorTest {

    @Mock
    private InboxRepository inboxRepository;

    @Mock
    private InboxStatusUpdater inboxStatusUpdater;

    @Mock
    private PlatformTransactionManager transactionManager;

    private InboxProcessor inboxProcessor;

    private UUID eventId;
    private UUID aggregateId;
    private final TestPayload payload = new TestPayload("test-data");

    @BeforeEach
    void setUp() {
        inboxProcessor = new InboxProcessor(
                inboxRepository,
                inboxStatusUpdater,
                new TransactionTemplate(transactionManager),
                new ObjectMapper()
        );
        eventId = UUID.randomUUID();
        aggregateId = UUID.randomUUID();
    }

    private Inbox inbox() {
        return Inbox.create(eventId, EventType.FUNDING_COMPLETED, aggregateId, "{}");
    }

    @Nested
    @DisplayName("이벤트 처리 성공")
    class Success {

        @Test
        @DisplayName("새 이벤트는 수신 기록 후 비즈니스 로직을 한 번 실행하고 처리 완료로 바꾼다")
        void newEvent_processedOnce() {
            Inbox received = inbox();
            when(inboxStatusUpdater.recordReceived(any(Inbox.class))).thenReturn(true);
            when(inboxRepository.findByIdForUpdate(eventId)).thenReturn(Optional.of(received));
            AtomicInteger executed = new AtomicInteger();

            inboxProcessor.processEvent(eventId, EventType.FUNDING_COMPLETED, aggregateId, payload,
                    p -> executed.incrementAndGet());

            assertThat(executed.get()).isEqualTo(1);
            assertThat(received.isProcessed()).isTrue();
            verify(inboxStatusUpdater, never()).markAsFailed(any(), any());
        }

        @Test
        @DisplayName("이전에 실패한 이벤트가 다시 오면 다시 처리한다")
        void failedEvent_retried() {
            Inbox failed = inbox();
            failed.markAsFailed("이전 실패");
            when(inboxStatusUpdater.recordReceived(any(Inbox.class))).thenReturn(false);
            when(inboxRepository.findByIdForUpdate(eventId)).thenReturn(Optional.of(failed));
            AtomicInteger executed = new AtomicInteger();

            inboxProcessor.processEvent(eventId, EventType.FUNDING_COMPLETED, aggregateId, payload,
                    p -> executed.incrementAndGet());

            assertThat(executed.get()).isEqualTo(1);
            assertThat(failed.isProcessed()).isTrue();
        }
    }

    @Nested
    @DisplayName("중복 이벤트")
    class DuplicateEvent {

        @Test
        @DisplayName("이미 처리 완료된 이벤트는 비즈니스 로직을 실행하지 않는다")
        void processedEvent_skipped() {
            Inbox processed = inbox();
            processed.markAsProcessed();
            when(inboxStatusUpdater.recordReceived(any(Inbox.class))).thenReturn(false);
            when(inboxRepository.findByIdForUpdate(eventId)).thenReturn(Optional.of(processed));
            AtomicInteger executed = new AtomicInteger();

            inboxProcessor.processEvent(eventId, EventType.FUNDING_COMPLETED, aggregateId, payload,
                    p -> executed.incrementAndGet());

            assertThat(executed.get()).isZero();
        }
    }

    @Nested
    @DisplayName("비즈니스 로직 실패")
    class BusinessLogicFailure {

        @Test
        @DisplayName("예외를 다시 던지고, 실패를 별도 트랜잭션으로 기록한다")
        void failure_recordedAndPropagated() {
            when(inboxStatusUpdater.recordReceived(any(Inbox.class))).thenReturn(true);
            when(inboxRepository.findByIdForUpdate(eventId)).thenReturn(Optional.of(inbox()));

            assertThatThrownBy(() -> inboxProcessor.processEvent(eventId, EventType.FUNDING_COMPLETED,
                    aggregateId, payload, p -> { throw new RuntimeException("비즈니스 로직 실패"); }))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("비즈니스 로직 실패");

            verify(inboxStatusUpdater).markAsFailed(eq(eventId), eq("비즈니스 로직 실패"));
        }
    }

    record TestPayload(String data) {}
}

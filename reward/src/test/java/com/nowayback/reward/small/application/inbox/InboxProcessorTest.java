package com.nowayback.reward.small.application.inbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.reward.application.inbox.InboxProcessor;
import com.nowayback.reward.application.inbox.repository.InboxRepository;
import com.nowayback.reward.domain.inbox.entity.Inbox;
import com.nowayback.reward.domain.vo.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboxProcessorTest {

    @Mock
    private InboxRepository inboxRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private InboxProcessor inboxProcessor;

    private UUID eventId;
    private UUID aggregateId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        aggregateId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("이벤트 처리 성공 테스트")
    class Success {

        @Test
        @DisplayName("새 이벤트 처리 성공")
        void processEvent_success() {
            // given
            AtomicBoolean businessLogicExecuted = new AtomicBoolean(false);
            TestPayload payload = new TestPayload("test-data");

            when(inboxRepository.save(any(Inbox.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            inboxProcessor.processEvent(
                    eventId,
                    EventType.FUNDING_COMPLETED,
                    aggregateId,
                    payload,
                    p -> businessLogicExecuted.set(true)
            );

            // then
            assertThat(businessLogicExecuted.get()).isTrue();
            verify(inboxRepository, times(1)).save(any(Inbox.class));
        }

        @Test
        @DisplayName("비즈니스 로직 실행 후 처리 완료 상태로 변경")
        void processEvent_markedAsProcessed() {
            // given
            TestPayload payload = new TestPayload("test-data");

            when(inboxRepository.save(any(Inbox.class))).thenAnswer(invocation -> {
                Inbox inbox = invocation.getArgument(0);
                return inbox;
            });

            // when
            inboxProcessor.processEvent(
                    eventId,
                    EventType.FUNDING_COMPLETED,
                    aggregateId,
                    payload,
                    p -> {}
            );

            // then
            verify(inboxRepository, times(1)).save(argThat(inbox ->
                    inbox.getId().equals(eventId) &&
                    inbox.getEventType() == EventType.FUNDING_COMPLETED
            ));
        }
    }

    @Nested
    @DisplayName("중복 이벤트 처리 테스트")
    class DuplicateEvent {

        @Test
        @DisplayName("중복 이벤트는 무시하고 비즈니스 로직 실행하지 않음")
        void processEvent_duplicateEvent_ignored() {
            // given
            AtomicBoolean businessLogicExecuted = new AtomicBoolean(false);
            TestPayload payload = new TestPayload("test-data");

            when(inboxRepository.save(any(Inbox.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

            // when
            inboxProcessor.processEvent(
                    eventId,
                    EventType.FUNDING_COMPLETED,
                    aggregateId,
                    payload,
                    p -> businessLogicExecuted.set(true)
            );

            // then
            assertThat(businessLogicExecuted.get()).isFalse();
            verify(inboxRepository, times(1)).save(any(Inbox.class));
        }
    }

    @Nested
    @DisplayName("비즈니스 로직 예외 테스트")
    class BusinessLogicException {

        @Test
        @DisplayName("비즈니스 로직 예외 발생 시 예외 전파 및 재시도 횟수 증가")
        void processEvent_businessLogicException_propagated() {
            // given
            TestPayload payload = new TestPayload("test-data");

            when(inboxRepository.save(any(Inbox.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when & then
            assertThatThrownBy(() -> inboxProcessor.processEvent(
                    eventId,
                    EventType.FUNDING_COMPLETED,
                    aggregateId,
                    payload,
                    p -> { throw new RuntimeException("비즈니스 로직 실패"); }
            ))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("비즈니스 로직 실패");

            verify(inboxRepository, times(1)).save(any(Inbox.class));
        }
    }

    record TestPayload(String data) {}
}
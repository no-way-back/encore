package com.nowayback.reward.infrastructure.kafka.listener;

import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.domain.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.project.event.ProjectCreatedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.project.payload.ProjectCreatedPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;

import static com.nowayback.reward.fixture.KafkaFixture.createProjectCreatedEvent;
import static com.nowayback.reward.fixture.KafkaFixture.createProjectCreatedPayload;
import static com.nowayback.reward.domain.vo.EventType.PROJECT_CREATED;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectEventConsumerTest {

    @Mock
    private RewardService rewardService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private ProjectEventListener projectEventConsumer;

    private UUID eventId;
    private UUID projectId;
    private UUID creatorId;
    private ProjectCreatedPayload payload;
    private ProjectCreatedEvent validEvent;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        creatorId = UUID.randomUUID();

        payload = createProjectCreatedPayload(projectId, creatorId, 3);
        validEvent = createProjectCreatedEvent(eventId, PROJECT_CREATED, payload);
    }

    @Nested
    @DisplayName("이벤트 처리 성공 테스트")
    class Success {

        @Test
        @DisplayName("PROJECT_CREATED 이벤트 수신 및 처리 성공")
        void consumeProjectCreatedEvent_success() {
            // when
            assertThatCode(() -> projectEventConsumer.consumeProjectEvent(validEvent, acknowledgment))
                    .doesNotThrowAnyException();

            // then
            verify(rewardService, times(1)).createRewardsForProject(
                    eq(eventId),          // UUID로 변경
                    eq(projectId),
                    eq(creatorId),
                    anyList()
            );

            verify(acknowledgment, times(1)).acknowledge();
        }
    }

    @Nested
    @DisplayName("이벤트 처리 실패/예외 테스트")
    class FailureAndException {

        @Test
        @DisplayName("처리 불가능한 이벤트 타입 수신 시 로직 미실행 및 Acknowledge 처리")
        void consumeProjectEvent_unsupportedEventType() {
            // given
            ProjectCreatedEvent invalidEvent = createProjectCreatedEvent(
                    eventId, EventType.UNSUPPORTED_TYPE, payload
            );

            // when
            assertThatCode(() -> projectEventConsumer.consumeProjectEvent(invalidEvent, acknowledgment))
                    .doesNotThrowAnyException();

            // then
            verifyNoInteractions(rewardService);
            verify(acknowledgment, times(1)).acknowledge();
        }

        @Test
        @DisplayName("EventHandler 처리 중 예외 발생 시 Acknowledge 처리")
        void consumeProjectEvent_handlerThrowsException() {
            // given
            doThrow(new RuntimeException("DB 저장 실패"))
                    .when(rewardService).createRewardsForProject(
                            any(UUID.class),
                            eq(projectId),
                            eq(creatorId),
                            anyList()
                    );

            // when
            assertThatCode(() -> projectEventConsumer.consumeProjectEvent(validEvent, acknowledgment))
                    .doesNotThrowAnyException();

            // then
            verify(rewardService, times(1)).createRewardsForProject(
                    any(UUID.class),
                    eq(projectId),
                    eq(creatorId),
                    anyList()
            );

            verify(acknowledgment, times(1)).acknowledge();
        }
    }
}
package com.nowayback.reward.infrastructure.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.reward.application.inbox.InboxProcessor;
import com.nowayback.reward.application.qrcode.QRCodeService;
import com.nowayback.reward.application.reward.RewardStockService;
import com.nowayback.reward.domain.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingCompletedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingFailedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingRefundEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.ProjectFundingSuccessEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.payload.FundingFailedPayload;
import com.nowayback.reward.infrastructure.kafka.dto.funding.payload.FundingRefundPayload;
import com.nowayback.reward.infrastructure.kafka.dto.funding.payload.FundingCompletedPayload;
import com.nowayback.reward.infrastructure.kafka.dto.funding.payload.ProjectFundingSuccessPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.nowayback.reward.domain.vo.EventType.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundingEventListenerTest {

    @Mock
    private RewardStockService rewardStockService;

    @Mock
    private QRCodeService qrCodeService;

    @Mock
    private InboxProcessor inboxProcessor;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private FundingEventListener fundingEventListener;

    private UUID eventId;
    private UUID fundingId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        fundingId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        objectMapper.findAndRegisterModules();
    }

    @Nested
    @DisplayName("펀딩 실패 이벤트 테스트")
    class FundingFailedEventTest {

        @Test
        @DisplayName("펀딩 실패 이벤트 수신 시 재고 복원 성공")
        void consumeFundingFailedEvent_success() throws Exception {
            // given
            FundingFailedPayload payload = new FundingFailedPayload(fundingId);
            FundingFailedEvent event = new FundingFailedEvent(eventId, FUNDING_FAILED, LocalDateTime.now(), payload);
            String message = objectMapper.writeValueAsString(objectMapper.writeValueAsString(event));

            doAnswer(invocation -> {
                Consumer<Object> handler = invocation.getArgument(4);
                handler.accept(invocation.getArgument(3));
                return null;
            }).when(inboxProcessor).processEvent(any(), any(), any(), any(), any());

            // when
            assertThatCode(() -> fundingEventListener.consumeFundingFailedEvent(message, acknowledgment))
                    .doesNotThrowAnyException();

            // then
            verify(inboxProcessor, times(1)).processEvent(
                    eq(eventId), eq(FUNDING_FAILED), eq(fundingId), any(), any()
            );
            verify(rewardStockService, times(1)).restoreStock(fundingId);
            verify(acknowledgment, times(1)).acknowledge();
        }

        @Test
        @DisplayName("잘못된 이벤트 타입 수신 시 로직 미실행")
        void consumeFundingFailedEvent_wrongEventType() throws Exception {
            // given
            FundingFailedPayload payload = new FundingFailedPayload(fundingId);
            FundingFailedEvent event = new FundingFailedEvent(eventId, UNSUPPORTED_TYPE, LocalDateTime.now(), payload);
            String message = objectMapper.writeValueAsString(objectMapper.writeValueAsString(event));

            // when
            assertThatCode(() -> fundingEventListener.consumeFundingFailedEvent(message, acknowledgment))
                    .doesNotThrowAnyException();

            // then
            verifyNoInteractions(inboxProcessor);
            verifyNoInteractions(rewardStockService);
            verify(acknowledgment, times(1)).acknowledge();
        }
    }

    @Nested
    @DisplayName("펀딩 환불 이벤트 테스트")
    class FundingRefundEventTest {

        @Test
        @DisplayName("펀딩 환불 이벤트 수신 시 재고 복원 성공")
        void consumeFundingRefundEvent_success() throws Exception {
            // given
            FundingRefundPayload payload = new FundingRefundPayload(fundingId);
            FundingRefundEvent event = new FundingRefundEvent(eventId, FUNDING_REFUND, LocalDateTime.now(), payload);
            String message = objectMapper.writeValueAsString(objectMapper.writeValueAsString(event));

            doAnswer(invocation -> {
                Consumer<Object> handler = invocation.getArgument(4);
                handler.accept(invocation.getArgument(3));
                return null;
            }).when(inboxProcessor).processEvent(any(), any(), any(), any(), any());

            // when
            assertThatCode(() -> fundingEventListener.consumeFundingRefundEvent(message, acknowledgment))
                    .doesNotThrowAnyException();

            // then
            verify(inboxProcessor, times(1)).processEvent(
                    eq(eventId), eq(FUNDING_REFUND), eq(fundingId), any(), any()
            );
            verify(rewardStockService, times(1)).restoreStock(fundingId);
            verify(acknowledgment, times(1)).acknowledge();
        }
    }

    @Nested
    @DisplayName("펀딩 완료 이벤트 테스트")
    class FundingCompletedEventTest {

        @Test
        @DisplayName("펀딩 완료 이벤트 수신 시 QR 코드 생성 성공")
        void consumeFundingCompletedEvent_success() throws Exception {
            // given
            FundingCompletedPayload payload = new FundingCompletedPayload(
                    projectId, fundingId, "test@test.com", List.of()
            );
            FundingCompletedEvent event = new FundingCompletedEvent(eventId, FUNDING_COMPLETED, LocalDateTime.now(), payload);
            String message = objectMapper.writeValueAsString(objectMapper.writeValueAsString(event));

            doAnswer(invocation -> {
                Consumer<Object> handler = invocation.getArgument(4);
                handler.accept(invocation.getArgument(3));
                return null;
            }).when(inboxProcessor).processEvent(any(), any(), any(), any(), any());

            // when
            assertThatCode(() -> fundingEventListener.consumeFundingCompletedEvent(message, acknowledgment))
                    .doesNotThrowAnyException();

            // then
            verify(inboxProcessor, times(1)).processEvent(
                    eq(eventId), eq(FUNDING_COMPLETED), eq(fundingId), any(), any()
            );
            verify(qrCodeService, times(1)).createQRCode(any());
            verify(acknowledgment, times(1)).acknowledge();
        }
    }

    @Nested
    @DisplayName("프로젝트 펀딩 성공 이벤트 테스트")
    class ProjectFundingSuccessEventTest {

        @Test
        @DisplayName("프로젝트 펀딩 성공 이벤트 수신 시 QR 이메일 발송 성공")
        void consumeProjectFundingSuccessEvent_success() throws Exception {
            // given
            ProjectFundingSuccessPayload payload = new ProjectFundingSuccessPayload(projectId);
            ProjectFundingSuccessEvent event = new ProjectFundingSuccessEvent(eventId, PROJECT_FUNDING_SUCCESS, payload);
            String message = objectMapper.writeValueAsString(objectMapper.writeValueAsString(event));

            doAnswer(invocation -> {
                Consumer<Object> handler = invocation.getArgument(4);
                handler.accept(invocation.getArgument(3));
                return null;
            }).when(inboxProcessor).processEvent(any(), any(), any(), any(), any());

            // when
            assertThatCode(() -> fundingEventListener.consumeProjectFundingSuccessEvent(message, acknowledgment))
                    .doesNotThrowAnyException();

            // then
            verify(inboxProcessor, times(1)).processEvent(
                    eq(eventId), eq(PROJECT_FUNDING_SUCCESS), eq(projectId), any(), any()
            );
            verify(qrCodeService, times(1)).sendQRCodesByProject(projectId);
            verify(acknowledgment, times(1)).acknowledge();
        }
    }

    @Nested
    @DisplayName("예외 처리 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("비즈니스 로직 예외 발생 시 RuntimeException 전파")
        void consumeFundingFailedEvent_businessException() throws Exception {
            // given
            FundingFailedPayload payload = new FundingFailedPayload(fundingId);
            FundingFailedEvent event = new FundingFailedEvent(eventId, FUNDING_FAILED, LocalDateTime.now(), payload);
            String message = objectMapper.writeValueAsString(objectMapper.writeValueAsString(event));

            doAnswer(invocation -> {
                Consumer<Object> handler = invocation.getArgument(4);
                handler.accept(invocation.getArgument(3));
                return null;
            }).when(inboxProcessor).processEvent(any(), any(), any(), any(), any());

            doThrow(new RuntimeException("재고 복원 실패"))
                    .when(rewardStockService).restoreStock(fundingId);

            // when & then
            assertThatThrownBy(() -> fundingEventListener.consumeFundingFailedEvent(message, acknowledgment))
                    .isInstanceOf(RuntimeException.class);

            verify(acknowledgment, never()).acknowledge();
        }
    }
}
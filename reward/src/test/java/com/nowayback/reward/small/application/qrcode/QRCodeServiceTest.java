package com.nowayback.reward.small.application.qrcode;

import com.nowayback.reward.application.port.ProjectClient;
import com.nowayback.reward.application.qrcode.QRCodeMailService;
import com.nowayback.reward.application.qrcode.QRCodeService;
import com.nowayback.reward.application.qrcode.TicketQRCodeGenerator;
import com.nowayback.reward.application.qrcode.command.CreateQRCodeCommand;
import com.nowayback.reward.application.qrcode.dto.QRCodeUseResult;
import com.nowayback.reward.application.qrcode.repository.QRCodeRepository;
import com.nowayback.reward.domain.exception.RewardErrorCode;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import com.nowayback.reward.domain.qrcode.vo.QrCodeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QRCodeServiceTest {

    @Mock
    private QRCodeRepository qrCodeRepository;

    @Mock
    private QRCodeMailService qrCodeMailService;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private TicketQRCodeGenerator ticketQRCodeGenerator;

    @InjectMocks
    private QRCodeService qrCodeService;

    private UUID projectId;
    private UUID fundingId;
    private UUID rewardId;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        fundingId = UUID.randomUUID();
        rewardId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("QR 코드 생성 테스트")
    class CreateQRCode {

        @Test
        @DisplayName("QR 코드 생성 성공")
        void createQRCode_success() {
            // given
            CreateQRCodeCommand command = new CreateQRCodeCommand(
                    projectId, fundingId, "test@test.com", List.of()
            );
            String projectTitle = "테스트 프로젝트";
            List<QRCodes> qrCodes = List.of(
                    createQRCode(UUID.randomUUID(), "test@test.com", "리워드1")
            );

            when(projectClient.getProjectTitle(projectId)).thenReturn(projectTitle);
            when(ticketQRCodeGenerator.generateFromPurchasedRewards(command, projectTitle)).thenReturn(qrCodes);

            // when
            qrCodeService.createQRCode(command);

            // then
            verify(projectClient, times(1)).getProjectTitle(projectId);
            verify(ticketQRCodeGenerator, times(1)).generateFromPurchasedRewards(command, projectTitle);
            verify(qrCodeRepository, times(1)).saveAll(qrCodes);
        }
    }

    @Nested
    @DisplayName("QR 코드 사용 테스트")
    class UseQRCode {

        @Test
        @DisplayName("QR 코드 사용 성공")
        void useQRCode_success() {
            // given
            UUID qrCodeId = UUID.randomUUID();
            QRCodes qrCode = createQRCode(qrCodeId, "test@test.com", "리워드1");

            when(qrCodeRepository.findById(qrCodeId)).thenReturn(Optional.of(qrCode));

            // when
            QRCodeUseResult result = qrCodeService.useQRCode(qrCodeId);

            // then
            assertThat(result).isNotNull();
            assertThat(qrCode.getStatus()).isEqualTo(QrCodeStatus.USED);
            verify(qrCodeRepository, times(1)).findById(qrCodeId);
        }

        @Test
        @DisplayName("존재하지 않는 QR 코드 사용 시 예외 발생")
        void useQRCode_notFound() {
            // given
            UUID qrCodeId = UUID.randomUUID();
            when(qrCodeRepository.findById(qrCodeId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> qrCodeService.useQRCode(qrCodeId))
                    .isInstanceOf(RewardException.class)
                    .extracting("errorCode")
                    .isEqualTo(RewardErrorCode.QRCODE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("프로젝트별 QR 코드 이메일 발송 테스트")
    class SendQRCodesByProject {

        @Test
        @DisplayName("프로젝트별 QR 코드 이메일 발송 성공")
        void sendQRCodesByProject_success() {
            // given
            QRCodes qrCode1 = createQRCode(UUID.randomUUID(), "user1@test.com", "리워드1");
            QRCodes qrCode2 = createQRCode(UUID.randomUUID(), "user1@test.com", "리워드2");
            QRCodes qrCode3 = createQRCode(UUID.randomUUID(), "user2@test.com", "리워드1");
            List<QRCodes> qrCodes = List.of(qrCode1, qrCode2, qrCode3);

            when(qrCodeRepository.findByProjectId(projectId)).thenReturn(qrCodes);

            // when
            qrCodeService.sendQRCodesByProject(projectId);

            // then
            verify(qrCodeRepository, times(1)).findByProjectId(projectId);
            verify(qrCodeMailService, times(1)).sendQRCodeEmail(eq("user1@test.com"), anyList());
            verify(qrCodeMailService, times(1)).sendQRCodeEmail(eq("user2@test.com"), anyList());
        }

        @Test
        @DisplayName("QR 코드가 없는 프로젝트는 이메일 발송하지 않음")
        void sendQRCodesByProject_noQRCodes() {
            // given
            when(qrCodeRepository.findByProjectId(projectId)).thenReturn(List.of());

            // when
            qrCodeService.sendQRCodesByProject(projectId);

            // then
            verify(qrCodeRepository, times(1)).findByProjectId(projectId);
            verifyNoInteractions(qrCodeMailService);
        }
    }

    private QRCodes createQRCode(UUID id, String email, String title) {
        return QRCodes.createWithId(
                id, rewardId, fundingId, projectId, email, title, "https://example.com/qr.png"
        );
    }
}
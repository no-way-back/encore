package com.nowayback.reward.small.domain.qrcode.entity;

import com.nowayback.reward.domain.exception.RewardErrorCode;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import com.nowayback.reward.domain.qrcode.vo.QrCodeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QRCodesTest {

    private UUID qrCodeId;
    private UUID rewardId;
    private UUID fundingId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        qrCodeId = UUID.randomUUID();
        rewardId = UUID.randomUUID();
        fundingId = UUID.randomUUID();
        projectId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("QR 코드 생성 테스트")
    class CreateQRCode {

        @Test
        @DisplayName("QR 코드 생성 성공")
        void createWithId_success() {
            // given
            String email = "test@test.com";
            String title = "테스트 리워드";
            String qrCodeImageUrl = "https://example.com/qr.png";

            // when
            QRCodes qrCode = QRCodes.createWithId(
                    qrCodeId, rewardId, fundingId, projectId, email, title, qrCodeImageUrl
            );

            // then
            assertThat(qrCode.getId()).isEqualTo(qrCodeId);
            assertThat(qrCode.getRewardId().getId()).isEqualTo(rewardId);
            assertThat(qrCode.getFundingId().getId()).isEqualTo(fundingId);
            assertThat(qrCode.getProjectId().getId()).isEqualTo(projectId);
            assertThat(qrCode.getEmail()).isEqualTo(email);
            assertThat(qrCode.getTitle()).isEqualTo(title);
            assertThat(qrCode.getQrCodeImageUrl()).isEqualTo(qrCodeImageUrl);
            assertThat(qrCode.getStatus()).isEqualTo(QrCodeStatus.UNUSED);
            assertThat(qrCode.getUsedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("QR 코드 사용 테스트")
    class UseQRCode {

        @Test
        @DisplayName("QR 코드 사용 성공")
        void use_success() {
            // given
            QRCodes qrCode = createQRCode();
            assertThat(qrCode.getStatus()).isEqualTo(QrCodeStatus.UNUSED);

            // when
            qrCode.use();

            // then
            assertThat(qrCode.getStatus()).isEqualTo(QrCodeStatus.USED);
            assertThat(qrCode.getUsedAt()).isNotNull();
        }

        @Test
        @DisplayName("이미 사용된 QR 코드 재사용 시 예외 발생")
        void use_alreadyUsed() {
            // given
            QRCodes qrCode = createQRCode();
            qrCode.use();

            // when & then
            assertThatThrownBy(qrCode::use)
                    .isInstanceOf(RewardException.class)
                    .extracting("errorCode")
                    .isEqualTo(RewardErrorCode.QRCODE_ALREADY_USED);
        }
    }

    private QRCodes createQRCode() {
        return QRCodes.createWithId(
                qrCodeId, rewardId, fundingId, projectId,
                "test@test.com", "테스트 리워드", "https://example.com/qr.png"
        );
    }
}
package com.nowayback.reward.domain.qrcode.entity;

import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.qrcode.vo.QrCodeStatus;
import com.nowayback.reward.domain.vo.FundingId;
import com.nowayback.reward.domain.vo.RewardId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.nowayback.reward.domain.exception.RewardErrorCode.QRCODE_ALREADY_USED;
import static com.nowayback.reward.domain.exception.RewardErrorCode.QRCODE_CANCELLED;
import static com.nowayback.reward.domain.qrcode.vo.QrCodeStatus.*;

@Entity
@Table(name = "p_qr_codes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QRCodes {

    @Id
    private UUID id;

    @Embedded
    private RewardId rewardId;

    @Embedded
    private FundingId fundingId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String qrCodeImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private QrCodeStatus status = UNUSED;

    private LocalDateTime usedAt;

    @Builder
    private QRCodes(UUID id, RewardId rewardId, FundingId fundingId,
                    String email, String title, String qrCodeImageUrl) {
        this.id = id;
        this.rewardId = rewardId;
        this.fundingId = fundingId;
        this.email = email;
        this.title = title;
        this.qrCodeImageUrl = qrCodeImageUrl;
    }

    public static QRCodes createWithId(UUID id, UUID rewardId, UUID fundingId,
                                        String email, String title, String qrCodeImageUrl) {
        return QRCodes.builder()
                .id(id)
                .rewardId(RewardId.of(rewardId))
                .fundingId(FundingId.of(fundingId))
                .email(email)
                .title(title)
                .qrCodeImageUrl(qrCodeImageUrl)
                .build();
    }

    /**
     * qr code 사용처리
     */
    public void use() {
        validateUsable();
        this.status = USED;
        this.usedAt = LocalDateTime.now();
    }

    private void validateUsable() {
        if (this.status == USED) {
            throw new RewardException(QRCODE_ALREADY_USED);
        }
        if (this.status == CANCELLED) {
            throw new RewardException(QRCODE_CANCELLED);
        }
    }
}
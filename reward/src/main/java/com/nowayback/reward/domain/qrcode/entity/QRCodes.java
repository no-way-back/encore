package com.nowayback.reward.domain.qrcode.entity;

import com.nowayback.reward.domain.qrcode.vo.QrCodeStatus;
import com.nowayback.reward.domain.shared.BaseEntity;
import com.nowayback.reward.domain.vo.FundingId;
import com.nowayback.reward.domain.vo.RewardId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.nowayback.reward.domain.qrcode.vo.QrCodeStatus.*;

@Entity
@Table(name = "p_qr_codes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QRCodes extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    private RewardId rewardId;

    @Embedded
    private FundingId fundingId;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private QrCodeStatus status = UNUSED;

    @Builder
    public QRCodes(RewardId rewardId, FundingId fundingId, String email) {
        this.rewardId = rewardId;
        this.fundingId = fundingId;
        this.email = email;
    }

    public static QRCodes create(UUID rewardId, UUID fundingId, String email) {
        return QRCodes.builder()
                .rewardId(RewardId.of(rewardId))
                .fundingId(FundingId.of(fundingId))
                .email(email)
                .build();
    }
}
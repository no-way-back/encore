package com.nowayback.reward.infrastructure.repository.qrcode;

import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import com.nowayback.reward.domain.vo.FundingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QRCodeJpaRepository extends JpaRepository<QRCodes, UUID> {
    List<QRCodes> findByFundingId(FundingId fundingId);
}

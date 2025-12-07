package com.nowayback.reward.infrastructure.repository.qrcode;

import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QRCodeJpaRepository extends JpaRepository<QRCodes, UUID> {
}

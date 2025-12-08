package com.nowayback.reward.infrastructure.repository.qrcode;

import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import com.nowayback.reward.domain.qrcode.repository.QRCodeRepository;
import com.nowayback.reward.domain.vo.FundingId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class QRCodeRepositoryImpl implements QRCodeRepository {

    private final QRCodeJpaRepository jpaRepository;

    @Override
    public Optional<QRCodes> findById(UUID qrCodeId) {
        return jpaRepository.findById(qrCodeId);
    }

    public QRCodes save(QRCodes qrCode) {
        return jpaRepository.save(qrCode);
    }

    @Override
    public List<QRCodes> saveAll(List<QRCodes> qrCodes) {
        return jpaRepository.saveAll(qrCodes);
    }

    @Override
    public List<QRCodes> findByFundingId(UUID fundingId) {
        return jpaRepository.findByFundingId(FundingId.of(fundingId));
    }
}

package com.nowayback.reward.infrastructure.repository.qrcode;

import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import com.nowayback.reward.domain.qrcode.repository.QRCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QRCodeRepositoryImpl implements QRCodeRepository {

    private final QRCodeJpaRepository jpaRepository;

    public QRCodes save(QRCodes qrCode) {
        return jpaRepository.save(qrCode);
    }

    @Override
    public List<QRCodes> saveAll(List<QRCodes> qrCodes) {
        return jpaRepository.saveAll(qrCodes);
    }
}

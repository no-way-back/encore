package com.nowayback.reward.application.qrcode;

import com.nowayback.reward.application.qrcode.command.CreateQRCodeCommand;
import com.nowayback.reward.application.qrcode.dto.QRCodeUseResult;
import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import com.nowayback.reward.domain.qrcode.repository.QRCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.nowayback.reward.domain.exception.RewardErrorCode.QRCODE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final QRCodeRepository qrCodeRepository;
    private final RewardService rewardService;
    private final QRCodeMailService qrCodeMailService;

    /**
     * QR코드 생성
     * - UNUSED 상태로 저장
     */
    public void createQRCode(CreateQRCodeCommand command) {
        command.purchasedRewards().stream()
                .filter(pr -> rewardService.isTicketType(pr.rewardId()))
                .forEach(pr -> {
                    List<QRCodes> qrCodes = Stream.generate(
                                    () -> QRCodes.create(
                                            pr.rewardId(),
                                            command.fundingId(),
                                            command.email()
                                    ))
                            .limit(pr.purchasedQuantity())
                            .toList();

                    qrCodeRepository.saveAll(qrCodes);
                });
    }

    @Transactional
    public QRCodeUseResult useQRCode(UUID qrCodeId) {
        QRCodes qrCode = qrCodeRepository.findById(qrCodeId)
                .orElseThrow(() -> new RewardException(QRCODE_NOT_FOUND));

        qrCode.use();

        return QRCodeUseResult.of(qrCode);
    }

    /**
     * 펀딩 목표 달성 시 해당 펀딩의 모든 QR 코드를 이메일로 발송
     */
    @Transactional(readOnly = true)
    public void sendQRCodesByFunding(UUID fundingId) {
        log.info("펀딩 {} QR 코드 이메일 발송 시작", fundingId);

        List<QRCodes> qrCodes = qrCodeRepository.findByFundingId(fundingId);

        if (qrCodes.isEmpty()) {
            log.warn("펀딩 {}에 발송할 QR 코드가 없습니다", fundingId);
            return;
        }

        qrCodes.forEach(qrCode ->
                qrCodeMailService.sendQRCodeEmail(qrCode.getEmail(), qrCode.getId())
        );

        log.info("펀딩 {} QR 코드 이메일 발송 완료 - 총 {}개", fundingId, qrCodes.size());
    }
}
package com.nowayback.reward.application.qrcode;

import com.nowayback.reward.application.port.ProjectClient;
import com.nowayback.reward.application.qrcode.command.CreateQRCodeCommand;
import com.nowayback.reward.application.qrcode.dto.QRCodeUseResult;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import com.nowayback.reward.application.qrcode.repository.QRCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nowayback.reward.domain.exception.RewardErrorCode.QRCODE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final QRCodeRepository qrCodeRepository;
    private final QRCodeMailService qrCodeMailService;
    private final ProjectClient projectClient;
    private final TicketQRCodeGenerator ticketQRCodeGenerator;

    @Transactional
    public void createQRCode(CreateQRCodeCommand command) {
        String projectTitle = projectClient.getProjectTitle(command.projectId());
        log.info("프로젝트 제목 조회 완료: {}", projectTitle);

        List<QRCodes> qrCodes = ticketQRCodeGenerator.generateFromPurchasedRewards(command, projectTitle);

        qrCodeRepository.saveAll(qrCodes);
        log.info("총 {}개의 QR 코드 생성 완료", qrCodes.size());
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

        Map<String, List<QRCodes>> qrCodesByEmail = qrCodes.stream()
                .collect(Collectors.groupingBy(QRCodes::getEmail));

        qrCodesByEmail.forEach((email, codes) -> {
            qrCodeMailService.sendQRCodeEmail(email, codes);
            log.info("이메일 발송 완료 - 수신자: {}, QR 코드 {}개", email, codes.size());
        });

        log.info("펀딩 {} QR 코드 이메일 발송 완료 - 총 {}개", fundingId, qrCodes.size());
    }
}
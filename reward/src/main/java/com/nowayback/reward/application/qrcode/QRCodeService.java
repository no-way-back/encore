package com.nowayback.reward.application.qrcode;

import com.nowayback.reward.application.qrcode.command.CreateQRCodeCommand;
import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import com.nowayback.reward.domain.qrcode.repository.QRCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final QRCodeRepository qrCodeRepository;
    private final RewardService rewardService;

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
}

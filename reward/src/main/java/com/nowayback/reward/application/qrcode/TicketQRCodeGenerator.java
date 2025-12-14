package com.nowayback.reward.application.qrcode;

import com.nowayback.reward.application.port.QRCodeCreator;
import com.nowayback.reward.application.qrcode.command.CreateQRCodeCommand;
import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketQRCodeGenerator {

    private final RewardService rewardService;
    private final QRCodeCreator qrCodeCreator;

    public List<QRCodes> generateFromPurchasedRewards(CreateQRCodeCommand command, String projectTitle) {
        return command.purchasedRewards().stream()
                .filter(pr -> rewardService.isTicketType(pr.rewardId()))
                .flatMap(pr -> {
                    log.info("티켓형 리워드 {} - {}개 QR 코드 생성 시작", pr.rewardId(), pr.purchasedQuantity());

                    return Stream.generate(() ->
                            qrCodeCreator.createCode(
                                    pr.rewardId(),
                                    command.fundingId(),
                                    command.email(),
                                    projectTitle
                            )
                    ).limit(pr.purchasedQuantity());
                })
                .toList();
    }
}
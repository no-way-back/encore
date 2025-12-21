package com.nowayback.reward.application.qrcode;

import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nowayback.reward.domain.exception.RewardErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRCodeMailService {

    private final JavaMailSender mailSender;
    private final QRCodeEmailTemplateBuilder templateBuilder;

    /**
     * QR 코드 발급 이메일 발송
     */
    public void sendQRCodeEmail(String email, List<QRCodes> qrCodes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String projectTitle = qrCodes.get(0).getTitle();

            helper.setTo(email);
            helper.setSubject("[encore] " + projectTitle + " 입장권 QR 코드가 발급되었습니다");
            helper.setText(templateBuilder.buildQRCodeEmailContent(qrCodes, projectTitle), true);

            mailSender.send(message);
            log.info("QR 코드 이메일 발송 성공 - 수신자: {}, QR 코드 {}개", email, qrCodes.size());

        } catch (MessagingException e) {
            log.error("QR 코드 이메일 발송 실패 - 수신자: {}", email, e);
            throw new RewardException(QRCODE_IMAGE_GENERATION_FAILED);
        }
    }
}
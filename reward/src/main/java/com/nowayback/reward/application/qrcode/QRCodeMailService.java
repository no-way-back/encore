package com.nowayback.reward.application.qrcode;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRCodeMailService {

    private final JavaMailSender mailSender;

    /**
     * QR 코드 발급 이메일 발송
     */
    public void sendQRCodeEmail(String email, UUID qrCodeId, String projectTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[encore] " + projectTitle + " 입장권 QR 코드가 발급되었습니다");
            helper.setText(buildQRCodeEmailContent(qrCodeId, projectTitle), true);

            mailSender.send(message);
            log.info("QR 코드 이메일 발송 성공 - 수신자: {}, QR ID: {}", email, qrCodeId);

        } catch (MessagingException e) {
            log.error("QR 코드 이메일 발송 실패 - 수신자: {}, QR ID: {}", email, qrCodeId, e);
            throw new RuntimeException("이메일 발송에 실패했습니다", e);
        }
    }

    private String buildQRCodeEmailContent(UUID qrCodeId, String projectSubject) {
        return String.format("""
                <html>
                <body>
                    <h2>%s 입장권 QR 코드가 발급되었습니다!</h2>
                    <p>펀딩 성공을 축하드립니다.</p>
                    <div>
                        <p><strong>프로젝트:</strong> %s</p>
                        <p><strong>QR 코드 ID:</strong> %s</p>
                    </div>
                    <p>QR 코드는 입장 시 스캔하여 사용할 수 있습니다.</p>
                </body>
                </html>
                """,
                projectSubject,
                projectSubject,
                qrCodeId
        );
    }
}
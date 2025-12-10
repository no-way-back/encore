package com.nowayback.reward.application.qrcode;

import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRCodeMailService {

    private final JavaMailSender mailSender;

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
            helper.setText(buildQRCodeEmailContent(qrCodes, projectTitle), true);

            mailSender.send(message);
            log.info("QR 코드 이메일 발송 성공 - 수신자: {}, QR 코드 {}개", email, qrCodes.size());

        } catch (MessagingException e) {
            log.error("QR 코드 이메일 발송 실패 - 수신자: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다", e);
        }
    }

    private String buildQRCodeEmailContent(List<QRCodes> qrCodes, String projectTitle) {
        StringBuilder content = new StringBuilder();

        content.append(String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2>%s 입장권 QR 코드가 발급되었습니다!</h2>
                    <p>펀딩 성공을 축하드립니다.</p>
                    <p><strong>프로젝트:</strong> %s</p>
                    <p><strong>발급 티켓 수:</strong> %d개</p>
                    <hr>
                """, projectTitle, projectTitle, qrCodes.size()));

        for (int i = 0; i < qrCodes.size(); i++) {
            QRCodes qrCode = qrCodes.get(i);
            content.append(String.format("""
                    <div style="margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px;">
                        <h3>티켓 %d</h3>
                        <img src="%s" alt="QR Code" style="width: 300px; height: 300px; display: block; margin: 10px 0;"/>
                        <p><strong>QR 코드 ID:</strong> %s</p>
                    </div>
                    """, i + 1, qrCode.getQrCodeImageUrl(), qrCode.getId()));
        }

        content.append("""
                    <hr>
                    <p>QR 코드는 입장 시 스캔하여 사용할 수 있습니다.</p>
                    <p style="color: #666; font-size: 12px;">이 이메일은 발신 전용입니다.</p>
                </body>
                </html>
                """);

        return content.toString();
    }
}
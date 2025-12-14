package com.nowayback.reward.application.qrcode;

import com.nowayback.reward.domain.qrcode.entity.QRCodes;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QRCodeEmailTemplateBuilder {

    public String buildQRCodeEmailContent(List<QRCodes> qrCodes, String projectTitle) {
        StringBuilder content = new StringBuilder();

        content.append(buildHeader(projectTitle, qrCodes.size()));

        for (QRCodes qrCode : qrCodes) {
            content.append(buildTicketSection(qrCode));
        }

        content.append(buildFooter());

        return content.toString();
    }

    private String buildHeader(String projectTitle, int ticketCount) {
        return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2>%s 입장권 QR 코드가 발급되었습니다!</h2>
                    <p>펀딩 성공을 축하드립니다.</p>
                    <p><strong>프로젝트:</strong> %s</p>
                    <p><strong>발급 티켓 수:</strong> %d개</p>
                    <hr>
                """, projectTitle, projectTitle, ticketCount);
    }

    private String buildTicketSection(QRCodes qrCode) {
        return String.format("""
                <div style="margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px;">
                    <img src="%s" alt="QR Code" style="width: 300px; height: 300px; display: block; margin: 10px 0;"/>
                </div>
                """, qrCode.getQrCodeImageUrl());
    }

    private String buildFooter() {
        return """
                <hr>
                <p>QR 코드는 입장 시 스캔하여 사용할 수 있습니다.</p>
                <p style="color: #666; font-size: 12px;">이 이메일은 발신 전용입니다.</p>
            </body>
            </html>
            """;
    }
}
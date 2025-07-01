package com.example.pet_noseprint_id.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendTempPassword(String toEmail, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[코도장] 임시 비밀번호 안내");
        message.setText("[ 코도장 임시 비밀번호 안내 ]\n임시 비밀번호: " + tempPassword +
                "\n로그인 후 꼭 비밀번호를 변경해주세요.");
        mailSender.send(message);
    }
    public void sendStyledTempPassword(String toEmail, String tempPassword) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("[코도장] 임시 비밀번호 안내");

            String htmlContent = """
            <div style="font-family: 'Arial', sans-serif; background-color: #f9f9f9; padding: 30px;">
                <div style="max-width: 500px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                    <h2 style="color: #2c3e50; text-align: center;">코도장 임시 비밀번호 안내</h2>
                    <p style="font-size: 16px; color: #333;">안녕하세요, 코도장입니다.</p>
                    <p style="font-size: 16px; color: #333;">
                        요청하신 임시 비밀번호를 아래에 안내드립니다. <br><br>
                        <strong style="display: inline-block; font-size: 18px; color: #e74c3c; background: #fceae9; padding: 10px 15px; border-radius: 5px;">%s</strong><br><br>
                        로그인 후 반드시 비밀번호를 변경해 주세요.
                    </p>
                    <div style="text-align: center; margin-top: 30px;">
                        <a href="https://yourdomain.com/login" style="padding: 10px 20px; background-color: #3498db; color: white; text-decoration: none; border-radius: 5px;">로그인하러 가기</a>
                    </div>
                    <p style="font-size: 12px; color: #aaa; text-align: center; margin-top: 20px;">
                        본 메일은 발신전용입니다. 문의사항은 웹사이트를 이용해주세요.
                    </p>
                </div>
            </div>
            """.formatted(tempPassword);

            helper.setText(htmlContent, true); // true for HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // 적절한 예외 처리 로직 추가
        }
    }
}

package newbie.place_review.api;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import newbie.place_review.module.verification.EmailVerificationModuleImpl;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Log4j2
@Service
@RequiredArgsConstructor
public class VerificationApi {

    private final JavaMailSender javaMailSender;

    private final EmailVerificationModuleImpl emailVerificationModule;

    /**
     * @param email
     * @return 인증코드 전송 성공: 201 Created<br> 인증코드 전송 실패: 500 Internal Server Error
     */
    public ApiResponse<Void> processEmailVerification(String email) {
        MimeMessage message = javaMailSender.createMimeMessage();

        String verificationCode = emailVerificationModule.issueVerificationCodeByEmail(email);

        try {
            configVerificationCodeMessage(message, email, verificationCode);
            javaMailSender.send(message);

            return ApiResponse.of("인증코드가 전송 되었습니다.", HttpStatus.CREATED);
        } catch (MessagingException e) {
            log.error("인증코드 전송에 실패하였습니다.");

            return ApiResponse.of("인증코드 전송 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void configVerificationCodeMessage(MimeMessage message, String to, String verificationCode) throws MessagingException {
        String verificationCodeHtml = createVerificationCodeHtml(verificationCode);

        message.setSubject("[Place Review] 이메일 인증코드");
        message.setSentDate(Date.valueOf(LocalDate.now()));
        message.addRecipients(Message.RecipientType.TO, to);
        message.setText(verificationCodeHtml, "utf-8", "html");
    }

    private String createVerificationCodeHtml(String verificationCode) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<p>인증코드: <strong>");
        stringBuilder.append(verificationCode);
        stringBuilder.append("</strong></p>");

        return stringBuilder.toString();
    }
}

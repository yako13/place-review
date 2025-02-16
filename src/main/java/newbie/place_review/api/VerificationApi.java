package newbie.place_review.api;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import newbie.place_review.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class VerificationApi {

    private final JavaMailSender javaMailSender;
    private final CacheManager cacheManager;


    private final long EMAIL_VERIFICATION_TIMEOUT = 300;
    private final long VERIFIED_EMAIL_TIMEOUT = 30;

    /**
     * @param email
     * @return 인증코드 전송 성공: 201 Created<br> 인증코드 전송 실패: 500 Internal Server Error
     */
    public ApiResponse<Void> processEmailVerification(String email) {
        MimeMessage message = javaMailSender.createMimeMessage();

        String verificationCode = getVerificationCode();
        cacheEmailAndVerificationCode(email, verificationCode);

        try {
            configVerificationCodeMessage(message, email, verificationCode);
            javaMailSender.send(message);

            return ApiResponse.of("인증코드가 전송 되었습니다.", HttpStatus.CREATED);
        } catch (MessagingException e) {
            log.error("인증코드 전송에 실패하였습니다.");

            return ApiResponse.of("인증코드 전송 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @return 인증코드 불일치: 401 Unauthorized<br>인증 성공: 200 OK<br>인증 진행 상태 아님: 400 Bad Request
     */
    public ApiResponse<Boolean> checkEmailVerificationCode(String email, String verificationCode) {
        Optional<String> optVerificationCode = Optional.ofNullable((String) cacheManager.get(email));

        return optVerificationCode.map(storedVerificationCode -> {
                                      if (!storedVerificationCode.equals(verificationCode)) {
                                          return ApiResponse.of("인증코드가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED, false);
                                      }

                                      cacheManager.getAndDelete(email);
                                      completeEmailVerification(email);

                                      return ApiResponse.of("이메일 인증 완료", HttpStatus.OK, true);
                                  })
                                  .orElseGet(() -> ApiResponse.of("이메일 인증이 진행되지 않았습니다.", HttpStatus.BAD_REQUEST, false));
    }

    private String getVerificationCode() {
        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
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

    private void cacheEmailAndVerificationCode(String email, Object verificationCode) {
        cacheManager.set(email, verificationCode, EMAIL_VERIFICATION_TIMEOUT, TimeUnit.SECONDS);
    }

    private void completeEmailVerification(String email) {
        cacheManager.set("!" + email, "verified", VERIFIED_EMAIL_TIMEOUT, TimeUnit.MINUTES);
    }
}

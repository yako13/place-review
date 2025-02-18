package newbie.place_review.module.verification;

import jakarta.mail.MessagingException;
import newbie.place_review.module.mail.MailModuleImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailVerificationModuleImpl {

    private final Random random = new Random();

    private final ValueOperations<String, Object> valueOperations;

    private final MailModuleImpl mailModule;

    public EmailVerificationModuleImpl(RedisTemplate<String, Object> redisTemplate, MailModuleImpl mailModule) {
        valueOperations = redisTemplate.opsForValue();
        this.mailModule = mailModule;
    }

    public void issueVerificationCodeByEmail(String email) throws MessagingException {

        final long EMAIL_VERIFICATION_TIMEOUT = 30;

        String verificationCOde = getVerificationCode();

        String subject = "[Place Review] 이메일 인증코드";
        String text = createVerificationCodeHtml(verificationCOde);

        try {
            mailModule.sendMimeMail(subject, text, email);
            valueOperations.set(email, verificationCOde, EMAIL_VERIFICATION_TIMEOUT, TimeUnit.MINUTES);
        } catch (MessagingException e) {
            throw new MessagingException("인증코드 전송에 실패하였습니다", e);
        }
    }

    public boolean checkEmailVerification(String email, String verificationCode) {

        String storedVerificationCode = (String) valueOperations.getAndDelete(email);

        // 저장된 인증코드가 없으면 false
        if (storedVerificationCode == null) return false;

        return storedVerificationCode.equals(verificationCode);
    }


    private String getVerificationCode() {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
    }

    private String createVerificationCodeHtml(String verificationCode) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<p>인증코드: <strong>");
        stringBuilder.append(verificationCode);
        stringBuilder.append("</strong></p>");

        return stringBuilder.toString();
    }
}

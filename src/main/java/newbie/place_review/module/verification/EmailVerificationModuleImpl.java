package newbie.place_review.module.verification;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailVerificationModuleImpl {

    private final ValueOperations<String, Object> valueOperations;

    public EmailVerificationModuleImpl(RedisTemplate<String, Object> redisTemplate) {
        valueOperations = redisTemplate.opsForValue();
    }

    public String issueVerificationCodeByEmail(String email) {

        final long EMAIL_VERIFICATION_TIMEOUT = 30;

        String verificationCOde = getVerificationCode();
        valueOperations.set(email, verificationCOde, EMAIL_VERIFICATION_TIMEOUT, TimeUnit.MINUTES);

        return verificationCOde;
    }

    public boolean checkEmailVerification(String email, String verificationCode) {

        String storedVerificationCode = (String) valueOperations.getAndDelete(email);

        // 저장된 인증코드가 없으면 false
        if (storedVerificationCode == null) return false;

        return storedVerificationCode.equals(verificationCode);
    }


    private String getVerificationCode() {
        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
    }
}

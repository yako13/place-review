package newbie.playground.api;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import newbie.playground.module.verification.EmailVerificationModuleImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class VerificationApi {

    private final EmailVerificationModuleImpl emailVerificationModule;

    /**
     * @param email
     * @return 인증코드 전송 성공: 201 Created<br> 인증코드 전송 실패: 500 Internal Server Error
     */
    public ApiResponse<Void> processEmailVerification(String email) {
        try {
            emailVerificationModule.issueVerificationCodeByEmail(email);

            return ApiResponse.of("인증코드가 전송 되었습니다.", HttpStatus.CREATED);
        } catch (MessagingException e) {
            log.error("인증코드 전송에 실패하였습니다.");

            return ApiResponse.of("인증코드 전송에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

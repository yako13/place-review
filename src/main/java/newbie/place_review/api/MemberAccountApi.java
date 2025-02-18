package newbie.place_review.api;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import newbie.place_review.dto.MemberDto;
import newbie.place_review.module.mail.MailModuleImpl;
import newbie.place_review.module.member.MemberModule;
import newbie.place_review.module.verification.EmailVerificationModuleImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class MemberAccountApi {

    private final Random random = new Random();

    private final EmailVerificationModuleImpl emailVerificationModule;

    private final PasswordEncoder passwordEncoder;

    private final MemberModule memberModule;
    private final MailModuleImpl mailModule;

    public ApiResponse<Void> signUp(String nickname, String email, String password, String verificationCode) {

        if (!emailVerificationModule.checkEmailVerification(email, verificationCode)) {
            return ApiResponse.of("이메일 인증코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        memberModule.save(email, passwordEncoder.encode(password), nickname);

        return ApiResponse.of("회원가입 성공", HttpStatus.CREATED);
    }

    public ApiResponse<Void> modifyAccount(String nickname, String email, String password, String verificationCode) {

        if (!emailVerificationModule.checkEmailVerification(email, verificationCode)) {
            return ApiResponse.of("이메일 인증코드가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        String originEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        return memberModule.getByEmail(originEmail)
                           .map(member -> {

                               member.setEmail(email);
                               member.setNickname(nickname);
                               member.setPassword(passwordEncoder.encode(password));

                               return ApiResponse.of("계정 수정 완료", HttpStatus.OK);
                           })
                           .orElseGet(() -> {
                               return ApiResponse.of("존재하지 않는 계정입니다.", HttpStatus.BAD_REQUEST);
                           });
    }

    public ApiResponse<? extends MemberDto> getCurrentMember() {

        String email = getCurrentMemberEmail();

        return memberModule.getByEmail(email)
                           .map(member -> {
                               MemberDto memberDto = MemberDto.builder()
                                                              .memberId(member.getId())
                                                              .email(member.getEmail())
                                                              .nickname(member.getNickname())
                                                              .build();

                               return ApiResponse.of("회원 정보를 성공적으로 불러왔습니다.", HttpStatus.OK, memberDto);
                           })
                           .orElseGet(() -> ApiResponse.of("회원 정보를 불러올 수 없습니다.", HttpStatus.NOT_FOUND, new MemberDto()));
    }

    public ApiResponse<Void> cancelAccount() {

        String email = getCurrentMemberEmail();

        memberModule.deleteByEmail(email);

        return ApiResponse.of("계정을 성공적으로 삭제하였습니다.", HttpStatus.NO_CONTENT);
    }

    public ApiResponse<Void> initializePassword(String email, String verificationCode) {

        if (!emailVerificationModule.checkEmailVerification(email, verificationCode)) {
            return ApiResponse.of("이메일 인증코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        return memberModule.getByEmail(email)
                           .map(member -> {

                               String initializedPassword = createRandomText(16);
                               String subject = "초기화된 비밀번호입니다.";
                               String text = String.format("초기화 된 패스워드: <strong>%s</strong>", initializedPassword);

                               try {
                                   mailModule.sendMimeMail(subject, text, email);

                                   String encodedPassword = passwordEncoder.encode(initializedPassword);
                                   member.setPassword(encodedPassword);

                                   return ApiResponse.of("비밀번호 초기화에 성공하였습니다.", HttpStatus.OK);
                               } catch (MessagingException e) {
                                   log.error("{}의 비밀번호 초기화에 실패하였습니다.", email);

                                   return ApiResponse.of("비밀번호 초기화에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                               }
                           })
                           .orElseGet(() -> {
                               return ApiResponse.of("찾을 수 없는 회원입니다.", HttpStatus.BAD_REQUEST);
                           });
    }

    private String getCurrentMemberEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String createRandomText(int length) {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int ascii = random.nextInt(33, 127);

            stringBuilder.append((char) ascii);
        }

        return stringBuilder.toString();
    }
}

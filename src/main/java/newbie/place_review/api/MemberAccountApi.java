package newbie.place_review.api;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import newbie.place_review.dto.MemberDto;
import newbie.place_review.module.member.MemberModule;
import newbie.place_review.module.verification.EmailVerificationModuleImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberAccountApi {

    private final EmailVerificationModuleImpl emailVerificationModule;

    private final PasswordEncoder passwordEncoder;

    private final MemberModule memberModule;

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

    private String getCurrentMemberEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

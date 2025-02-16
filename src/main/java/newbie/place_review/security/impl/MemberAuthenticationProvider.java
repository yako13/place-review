package newbie.place_review.security.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 회원 인증 제공자
 */
@Component
@RequiredArgsConstructor
public class MemberAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService; // MemberUserDetailsService가 여기로 주입 됨.

    private final PasswordEncoder passwordEncoder; // DelegatingPasswordEncode가 여기로 주입 됨.

    /**
     * @param authentication 필터에서 추출 된 인증
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName(); // 사용자 식별 값. ex) 아이디, 이메일
        String password = authentication.getCredentials().toString(); //  대체로 비밀번호

        UserDetails userDetails = userDetailsService.loadUserByUsername(username); // 사용자 정보를 가져옴
        
        // 비밀번호 일치하는 지 확인
        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            // 인증 성공하면 아래와 같은 Authentication 구현체를 반환 함
            // 아래의 UsernamePasswordAuthenticationToken은 생성하면 자동으로 인증 된 상태가 됨
            return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Invalid password!"); // 비밀번호 불일치하면 예외 던짐
        }
    }

    /**
     * 어떤 인증을 지원하는 지 이 메서드가 알려줌
     * @param authentication
     * @return 어떤 인증을 받아서 그걸 지원하면 true 아니면 false
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}

package newbie.place_review.security.impl;

import lombok.RequiredArgsConstructor;
import newbie.place_review.module.member.Member;
import newbie.place_review.module.member.MemberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 사용자 정보를 가져오는 역할을 함
 */
@Service
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService {

    // 회원 Repository
    private final MemberRepository memberRepository;

    /**
     * 저장소에서 회원 정보를 가져와서 Security가 사용하는 사용자 인터페이스인 UserDetails 구현체를 반환 함
     * @param username the username identifying the user whose data is required.
     * @return UserDetails 구현체를 반환, User는 UserDetails의 구현체임.
     * @throws UsernameNotFoundException // 사용자를 못 찾으면 예외 던짐
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new
                UsernameNotFoundException("User details not found for the user: " + username));

        // Security의 AuthenticationProvider에서 사용할 UserDetails의 구현체, User를 생성해서 반환함.
        return new User(member.getEmail(), member.getPassword(), Collections.emptyList());
    }
}

package newbie.place_review.security.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import newbie.place_review.module.member.Member;
import newbie.place_review.module.member.MemberRepository;
import newbie.place_review.security.OAuth2UserInfo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;

        if (provider.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }

        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();

        Optional<Member> optMember = memberRepository.findByEmail(email);


        return optMember.map(member -> new DefaultOAuth2User(Collections.emptyList(), oAuth2User.getAttributes(), "email"))
                        .orElseGet(() -> {
                            Member member = Member.builder()
                                                  .email(email)
                                                  .nickname(name)
                                                  .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                                  .build();

                            memberRepository.save(member);

                            return new DefaultOAuth2User(Collections.emptyList(), oAuth2User.getAttributes(), "email");
                        });
    }
}

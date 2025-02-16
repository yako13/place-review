package newbie.place_review.module.member.impl;

import lombok.RequiredArgsConstructor;
import newbie.place_review.module.member.Member;
import newbie.place_review.module.member.MemberModule;
import newbie.place_review.module.member.MemberRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberModuleImpl implements MemberModule {

    private final MemberRepository memberRepository;

    @Override
    public Member save(String email, String password, String nickname) {
        Assert.notNull(email, "Email cannot be null");
        Assert.notNull(password, "Password cannot be null");
        Assert.notNull(nickname, "Nickname cannot be null");

        Member member = Member.builder()
                              .email(email)
                              .password(password)
                              .nickname(nickname)
                              .build();

        return memberRepository.save(member);
    }

    @Override
    public void deleteById(Long memberId) {
        Assert.notNull(memberId, "MemberId cannot be null");

        Optional<Member> optMember = memberRepository.findById(memberId);

        optMember.ifPresentOrElse(memberRepository::delete,
                () -> {
                    throw new DataRetrievalFailureException("삭제할 회원을 찾을 수 없습니다.");
                });
    }

    @Override
    public Member update(Long memberId, String nickname, String email, String password) {
        Assert.notNull(memberId, "MemberId cannot be null");
        Assert.notNull(email, "Email cannot be null");
        Assert.notNull(nickname, "Nickname cannot be null");

        return memberRepository.findById(memberId).map(member -> {
            member.setNickname(nickname);
            member.setEmail(email);
            member.setPassword(password);

            return member;
        }).orElseThrow(() -> new DataRetrievalFailureException("수정할 회원을 찾을 수 없습니다."));
    }

    @Override
    public Optional<Member> getById(Long memberId) {
        Assert.notNull(memberId, "MemberId cannot be null");

        return memberRepository.findById(memberId);
    }

    @Override
    public Optional<Member> getByEmail(String email) {
        Assert.notNull(email, "Email cannot be null");

        return memberRepository.findByEmail(email);
    }
}

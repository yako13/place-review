package newbie.place_review.module.member;

import java.util.Optional;

public interface MemberModule {
    public Member save(String email, String password, String nickname);

    public Optional<Member> getById(Long memberId);

    public Optional<Member> getByEmail(String email);

    public void deleteById(Long memberId);

    public Member update(Long memberId, String nickname, String email, String password);

}

package newbie.place_review.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class MemberDto {

    private Long  memberId;

    private String email;

    private String nickname;

    @Builder
    public MemberDto(Long memberId, String email, String nickname) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
    }
}

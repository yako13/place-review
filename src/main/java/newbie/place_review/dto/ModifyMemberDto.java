package newbie.place_review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModifyMemberDto {

    private Long memberId;

    private String email;

    private String nickname;

    private String password;
}

package newbie.playground.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModifyMemberDto {

    private String email;

    private String nickname;

    private String password;

    private String verificationCode;
}

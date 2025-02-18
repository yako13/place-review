package newbie.playground.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpDto {

    private String email;

    private String password;

    private String nickname;

    private String verificationCode;
}

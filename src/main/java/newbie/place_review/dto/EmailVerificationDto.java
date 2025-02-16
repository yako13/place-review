package newbie.place_review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmailVerificationDto {

    private String email;

    private String verificationCode;
}

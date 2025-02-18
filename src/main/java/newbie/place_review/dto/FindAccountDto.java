package newbie.place_review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindAccountDto {

    private String email;

    private String verificationCode;
}

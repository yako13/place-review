package newbie.place_review.rest;

import lombok.RequiredArgsConstructor;
import newbie.place_review.api.ApiResponse;
import newbie.place_review.api.VerificationApi;
import newbie.place_review.dto.EmailVerificationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationApi verificationApi;

    @PostMapping("/verification/email")
    public ResponseEntity<ApiResponse<Void>> processEmailVerification(@RequestBody EmailVerificationDto emailVerificationDto) {

        String email = emailVerificationDto.getEmail();

        ApiResponse<Void> apiResponse = verificationApi.processEmailVerification(email);

        return ResponseEntity.status(apiResponse.getHttpStatus()).body(apiResponse);
    }

    @PostMapping("/verification/email/check")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailVerification(@RequestBody EmailVerificationDto emailVerificationDto) {

        String email = emailVerificationDto.getEmail();
        String verificationCode = emailVerificationDto.getVerificationCode();

        ApiResponse<Boolean> apiResponse = verificationApi.checkEmailVerificationCode(email, verificationCode);

        return ResponseEntity.status(apiResponse.getHttpStatus()).body(apiResponse);
    }

}

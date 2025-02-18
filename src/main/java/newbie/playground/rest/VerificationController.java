package newbie.playground.rest;

import lombok.RequiredArgsConstructor;
import newbie.playground.api.ApiResponse;
import newbie.playground.api.VerificationApi;
import newbie.playground.dto.EmailVerificationDto;
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
}

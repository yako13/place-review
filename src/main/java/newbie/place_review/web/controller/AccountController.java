package newbie.place_review.web.controller;

import lombok.RequiredArgsConstructor;
import newbie.place_review.api.ApiResponse;
import newbie.place_review.api.MemberAccountApi;
import newbie.place_review.dto.MemberDto;
import newbie.place_review.dto.ModifyMemberDto;
import newbie.place_review.dto.SignUpDto;
import newbie.place_review.web.handler.AccountModelHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final MemberAccountApi memberAccountApi;

    private final AccountModelHandler accountModelHandler;

    @GetMapping("/find/password")
    public String initFindPassword() {
        return "pages/account/find-password";
    }

    @GetMapping("/my-account")
    public String initMyAccount(Model model, RedirectAttributes redirectAttributes) {

        ApiResponse<? extends MemberDto> apiResponse = memberAccountApi.getCurrentMember();

        accountModelHandler.handleMyAccountView(apiResponse, model, redirectAttributes);

        if (apiResponse.getHttpStatus().isError()) {
            return "redirect:/feedback";
        }

        return "pages/account/my-account";
    }

    @PostMapping("/my-account")
    public String processMyAccount(ModifyMemberDto modifyMemberDto, RedirectAttributes redirectAttributes) {

        Long memberId = modifyMemberDto.getMemberId();
        String nickname = modifyMemberDto.getNickname();
        String email = modifyMemberDto.getEmail();
        String password = modifyMemberDto.getPassword();

        ApiResponse<Void> apiResponse = memberAccountApi.modifyAccount(memberId, nickname, email, password);

        if (apiResponse.getHttpStatus().isError()) {
            accountModelHandler.handleMyAccountView(apiResponse, redirectAttributes);

            return "redirect:/my-account";
        }

        return "redirect:/feedback";
    }

    @GetMapping("/sign-in")
    public String initSignIn() {
        return "pages/account/sign-in";
    }

    @GetMapping("/sign-up")
    public String initSignUp() {
        return "pages/account/sign-up";
    }

    @PostMapping("/sign-up")
    public String processSignUp(SignUpDto signUpDto, RedirectAttributes redirectAttributes) {

        String nickname = signUpDto.getNickname();
        String email = signUpDto.getEmail();
        String password = signUpDto.getPassword();

        ApiResponse<Void> apiResponse = memberAccountApi.signUp(nickname, email, password);

        accountModelHandler.handleSignUpView(apiResponse, redirectAttributes);

        if (apiResponse.getHttpStatus().isError()) {
            return "redirect:/sign-up";
        }

        return "redirect:/sign-in";
    }
}

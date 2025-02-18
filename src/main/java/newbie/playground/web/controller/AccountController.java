package newbie.playground.web.controller;

import lombok.RequiredArgsConstructor;
import newbie.playground.api.ApiResponse;
import newbie.playground.api.MemberAccountApi;
import newbie.playground.dto.FindAccountDto;
import newbie.playground.dto.MemberDto;
import newbie.playground.dto.ModifyMemberDto;
import newbie.playground.dto.SignUpDto;
import newbie.playground.web.handler.AccountModelHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final MemberAccountApi memberAccountApi;

    private final AccountModelHandler accountModelHandler;

    @GetMapping("/find/account")
    public String initFindAccount() {
        return "pages/account/find-account";
    }

    @PostMapping("/find/account")
    public String processFindAccount(FindAccountDto findAccountDto, RedirectAttributes redirectAttributes) {

        String email = findAccountDto.getEmail();
        String verificationCode = findAccountDto.getVerificationCode();

        ApiResponse<Void> apiResponse = memberAccountApi.initializePassword(email, verificationCode);

        if (apiResponse.getHttpStatus().isError()) {
            return "redirect:/find/account";
        }

        return "redirect:/sign-in";
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

        String nickname = modifyMemberDto.getNickname();
        String email = modifyMemberDto.getEmail();
        String password = modifyMemberDto.getPassword();
        String verificationCode = modifyMemberDto.getVerificationCode();

        ApiResponse<Void> apiResponse = memberAccountApi.modifyAccount(nickname, email, password, verificationCode);

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
        String verificationCode = signUpDto.getVerificationCode();

        ApiResponse<Void> apiResponse = memberAccountApi.signUp(nickname, email, password, verificationCode);

        accountModelHandler.handleSignUpView(apiResponse, redirectAttributes);

        if (apiResponse.getHttpStatus().isError()) {
            return "redirect:/sign-up";
        }

        return "redirect:/sign-in";
    }

    @GetMapping("/my-account/cancel")
    public String initCancelAccount() {
        return "pages/account/cancel-account";
    }

    @DeleteMapping("/my-account/cancel")
    public String processCancelAccount() {

        ApiResponse<Void> apiResponse = memberAccountApi.cancelAccount();

        if(apiResponse.getHttpStatus().isError()) {
            return  "redirect:/my-account";
        }

        return "redirect:/sign-out";
    }
}

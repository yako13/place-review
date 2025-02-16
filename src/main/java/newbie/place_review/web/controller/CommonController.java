package newbie.place_review.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {

    @GetMapping("/")
    public String initHome() {
        return "pages/common/home";
    }

    @GetMapping("/feedback")
    public String initFeedBack() {
        return "pages/common/feedback";
    }
}

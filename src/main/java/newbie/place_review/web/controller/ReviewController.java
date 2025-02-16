package newbie.place_review.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {

    @GetMapping("/review/write")
    public String initWriteReview() {
        return "pages/review/write-review";
    }

    @GetMapping("/review/modify")
    public String initModifyReview() {
        return "pages/review/modify-review";
    }
}

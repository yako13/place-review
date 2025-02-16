package newbie.place_review.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/live-chat")
    public String initLiveChat() {
        return "pages/chat/live-chat";
    }
}

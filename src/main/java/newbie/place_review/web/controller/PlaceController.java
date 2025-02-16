package newbie.place_review.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PlaceController {

    @GetMapping("/place/{placeId}")
    String initPlaceDetails(@PathVariable("placeId") Long placeId) {
        return "pages/place/place-details";
    }
}

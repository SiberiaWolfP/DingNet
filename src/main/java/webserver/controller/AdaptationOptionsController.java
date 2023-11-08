package webserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdaptationOptionsController {

    @GetMapping("/adaptation_options")
    public String adaptationOptions() {
        return "adaptation_options";
    }
}

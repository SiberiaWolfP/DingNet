package webserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webserver.DTO.AdaptationOptionsDTO;
import webserver.service.AdaptationOptionsService;

@RestController
public class AdaptationOptionsController {

    private final AdaptationOptionsService adaptationOptionsService;

    public AdaptationOptionsController(AdaptationOptionsService adaptationOptionsService) {
        this.adaptationOptionsService = adaptationOptionsService;
    }

    @GetMapping("/adaptation_options")
    public AdaptationOptionsDTO adaptationOptions() {
        return adaptationOptionsService.getAdaptationOptions();
    }
}

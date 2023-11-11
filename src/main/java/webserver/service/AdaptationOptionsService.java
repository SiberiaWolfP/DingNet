package webserver.service;

import org.springframework.stereotype.Service;
import webserver.DTO.AdaptationOptionsDTO;

@Service
public class AdaptationOptionsService {

    public AdaptationOptionsDTO getAdaptationOptions() {
        return new AdaptationOptionsDTO();
    }
}

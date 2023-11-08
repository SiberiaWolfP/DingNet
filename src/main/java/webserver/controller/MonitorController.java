package webserver.controller;

import iot.SimulationRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webserver.service.MonitorService;

@RestController
public class MonitorController {

    @GetMapping("/monitor")
    public SimulationRunner monitor() {
        return SimulationRunner.getInstance();
    }
}

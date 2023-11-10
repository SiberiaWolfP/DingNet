package webserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webserver.DTO.MonitorDTO;
import webserver.service.MonitorService;

@RestController
public class MonitorController {

    private final MonitorService monitorService;

    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping("/monitor")
    public MonitorDTO monitor() {
        return monitorService.collectMonitorData();
    }
}

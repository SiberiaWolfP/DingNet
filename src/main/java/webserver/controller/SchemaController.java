package webserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webserver.service.MonitorService;
import webserver.service.SchemaService;

@RestController
public class SchemaController {

    final
    SchemaService schemaService;

    public SchemaController(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @GetMapping("/monitor_schema")
    public String monitorSchema() {
        return schemaService.getMonitorSchema();
    }

    @GetMapping("/adaptation_options_schema")
    public String adaptationOptionsSchema() {
        return schemaService.getAdaptationOptionsSchema();
    }

    @GetMapping("/execute_schema")
    public String executeSchema() {
        return "execute_schema";
    }
}

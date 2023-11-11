package webserver.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webserver.DTO.ExecuteDTO;
import webserver.service.ExecuteService;

@RestController
public class ExecuteController {

    final ExecuteService executeService;

    public ExecuteController(ExecuteService executeService) {
        this.executeService = executeService;
    }

    @PutMapping("/execute")
    public ResponseEntity<String> execute(@Valid @RequestBody ExecuteDTO executeDTO) {
        if (!executeService.executeAdaptation(executeDTO)) {
            return ResponseEntity.badRequest().body("Invalid input.");
        }
        return ResponseEntity.ok("Adaptation executed.");
    }
}

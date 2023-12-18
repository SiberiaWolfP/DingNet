package webserver.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import webserver.DTO.WorldOptionsDTO;
import webserver.service.WorldService;

@RestController
public class WorldController {
    Logger logger = LoggerFactory.getLogger(WorldController.class);

    final
    WorldService worldService;

    public WorldController(WorldService worldService) {
        this.worldService = worldService;
    }

    @PostMapping("/init_world")
    public ResponseEntity<String> initWorld(@Valid @RequestBody WorldOptionsDTO worldOptionsDTO) {
        if (!worldService.initWorld(worldOptionsDTO)) {
            return ResponseEntity.badRequest().body("Invalid input.");
        }
        return ResponseEntity.ok("World initialized.");
    }

    @GetMapping("/start")
    public ResponseEntity<String> start() {
        try {
            worldService.start();
            return ResponseEntity.ok("Simulation started.");
        } catch (InterruptedException e) {
            logger.error("Error while starting simulation.", e);
            return ResponseEntity.badRequest().body("Error while starting simulation.");
        }
    }

    @GetMapping("/reset_entities")
    public ResponseEntity<String> resetEntities() {
        worldService.resetEntities();
        return ResponseEntity.ok("Entities reset.");
    }

    @GetMapping("/reset_gateways")
    public ResponseEntity<String> resetGateways() {
        worldService.resetGateways();
        return ResponseEntity.ok("Gateways reset.");
    }

    @GetMapping("/reset_mote")
    public ResponseEntity<String> resetMote() {
        worldService.resetMote();
        return ResponseEntity.ok("Mote reset.");
    }

    @GetMapping("/reset_map")
    public ResponseEntity<String> resetMap() {
        worldService.resetMap();
        return ResponseEntity.ok("Map reset.");
    }
}

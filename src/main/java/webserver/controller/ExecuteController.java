package webserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecuteController {

    @PutMapping("/execute")
    public String execute() {
        return "execute";
    }
}

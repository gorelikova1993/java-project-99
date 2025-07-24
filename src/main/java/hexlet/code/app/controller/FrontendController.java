package hexlet.code.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {
    @GetMapping("/")
    public String home() {
        return "index.html";  // index.html будет расположен в resources/templates
    }
}

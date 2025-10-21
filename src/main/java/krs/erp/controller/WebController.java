package krs.erp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Removed index mapping to allow static index.html to be served
    // The static index.html in resources/static/templates/ will be served for the root path
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
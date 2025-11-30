package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/welcome")
    public String welcome() {
        return "index";
    }

    @GetMapping("/home")
    public String home() {
        return "index";
    }

    @GetMapping("/permissionDenied")
    public String permissionDenied() {
        return "permissionDenied";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}

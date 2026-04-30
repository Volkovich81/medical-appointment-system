package com.medical.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/specializations", "/doctors", "/patients", "/appointments", "/medical-records"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
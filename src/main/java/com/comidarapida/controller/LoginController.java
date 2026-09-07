package com.comidarapida.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginGet() {
        return "login";
    }

    // Support GET logout to avoid 405 when a GET is issued; prefer POST /logout for proper logout.
    @GetMapping("/logout")
    public String logoutGet() {
        return "redirect:/login?logout";
    }
}

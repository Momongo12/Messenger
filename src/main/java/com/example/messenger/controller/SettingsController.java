package com.example.messenger.controller;

import com.example.messenger.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {

    @GetMapping("/settings")
    public String getSettingsPage(Authentication authentication,  Model model){
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("user", currentUser);

        return "settings";
    }
}

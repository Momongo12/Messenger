package com.example.messenger.controller;

import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.User;
import com.example.messenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SettingsController {

    @Autowired
    private UserService userService;

    @GetMapping("/settings")
    public String getSettingsPage(Authentication authentication,  Model model){
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("user", currentUser);
        return "settings";
    }

    @PutMapping("/settings")
    public ResponseEntity<String> updateUserSettings(Authentication authentication, @RequestBody UserInfoDto userInfoDto){
        try {
            userService.updateUserDetails(userInfoDto, (User) authentication.getPrincipal());
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(405)).build();
        }
    }
}

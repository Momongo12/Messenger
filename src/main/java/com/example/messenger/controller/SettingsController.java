package com.example.messenger.controller;

import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.User;
import com.example.messenger.service.ImageService;
import com.example.messenger.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * This is the SettingsController class that handles user settings functionality.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@Controller
@Log4j2
public class SettingsController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    /**
     * Handles the GET request for the settings page.
     *
     * @param authentication  the authentication object containing the current user's information
     * @param model           the model to be populated with data
     * @return the view name for rendering the settings page
     */
    @GetMapping("/settings")
    public String getSettingsPage(Authentication authentication, Model model) {
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("user", currentUser);
        model.addAttribute("avatarImageUrl", imageService.getAvatarImageUrlByUser(currentUser));
        return "settings";
    }

    /**
     * Handles the PUT request for updating user settings.
     *
     * @param authentication  the authentication object containing the current user's information
     * @param userInfoDto     the DTO object containing the updated user information
     * @return the ResponseEntity with the appropriate status code
     */
    @PutMapping("/settings")
    public ResponseEntity<String> updateUserSettings(Authentication authentication, @RequestBody UserInfoDto userInfoDto) {
        try {
            userService.updateUserDetails(userInfoDto, (User) authentication.getPrincipal());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
    }
}


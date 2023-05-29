package com.example.messenger.controller;

import com.example.messenger.model.User;
import com.example.messenger.service.ImageService;
import com.example.messenger.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


/**
 * This is the HomePageController class that handles requests related to the user's home page.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@Controller
@Log4j2
public class HomePageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    /**
     * Retrieves the home page for the authenticated user.
     *
     * @param authentication the authentication object containing the current user's information
     * @param model          the model to be populated with data
     * @return the view name for rendering the user's home page
     */
    @GetMapping("/home")
    public String getSelfHomePage(Authentication authentication, Model model) {
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("avatarImageUrl", imageService.getAvatarImageUrlByUser(currentUser));
        model.addAttribute("user", currentUser);
        model.addAttribute("profileBgImageUrl", imageService.getProfileBgImageUrlByUser(currentUser));

        return "selfHomePage";
    }

    /**
     * Retrieves the home page for a specific user.
     *
     * @param userId         the ID of the user
     * @param authentication the authentication object containing the current user's information
     * @param model          the model to be populated with data
     * @return the view name for rendering the user's home page
     */
    @GetMapping("/home/{userId}")
    public String getHomePage(@PathVariable("userId") Long userId, Authentication authentication, Model model) {
        User currentUser = (authentication != null) ? (User) authentication.getPrincipal() : null;
        User secondUser = userService.findUserByUserId(userId);

        if (currentUser != null && Objects.equals(currentUser.getUserId(), secondUser.getUserId())) {
            model.addAttribute("avatarImageUrl", imageService.getAvatarImageUrlByUser(currentUser));
            model.addAttribute("user", currentUser);
            model.addAttribute("profileBgImageUrl", imageService.getProfileBgImageUrlByUser(currentUser));

            return "selfHomePage";
        } else {
            model.addAttribute("avatarImageUrl", imageService.getAvatarImageUrlByUser(secondUser));
            model.addAttribute("user", secondUser);
            model.addAttribute("profileBgImageUrl", imageService.getProfileBgImageUrlByUser(secondUser));

            return "homePage";
        }
    }

    /**
     * Retrieves an image by its name.
     *
     * @param imageName the name of the image
     * @return a response entity with the image resource
     * @throws MalformedURLException if the image URL is malformed
     */
    @GetMapping("/image/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws MalformedURLException {
        Path imagePath = Paths.get("src/main/resources/images/" + imageName);

        Resource resource = new UrlResource(imagePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Uploads an image for the user.
     *
     * @param image          the image file to be uploaded
     * @param typeImage      the type of the image (avatar or background)
     * @param authentication the authentication object containing the current user's information
     * @return a response entity with the URL of the uploaded image
     */
    @PostMapping("/image/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile image, @RequestParam("typeImage") String typeImage,
                                              Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            if (typeImage.equals("avatar")) {
                String imageUrl = imageService.updateUserAvatarImageUrlByUser(currentUser, image);
                return ResponseEntity.ok().body(imageUrl);
            } else {
                String imageUrl = imageService.updateUserBgImageUrlByUser(currentUser, image);
                return ResponseEntity.ok().body(imageUrl);
            }
        } catch (IOException e) {
            log.error("Error occurred while uploading an image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a user's image.
     *
     * @param typeImage      the type of the image (avatar or background)
     * @param authentication the authentication object containing the current user's information
     * @return a response entity with the URL of the user's avatar image
     */
    @DeleteMapping("/image")
    public ResponseEntity<String> deleteUserImage(@RequestParam("typeImage") String typeImage, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        if (typeImage.equals("avatar")) {
            imageService.deleteAvatarImageByUser(currentUser);
        } else if (typeImage.equals("bgImage")) {
            imageService.deleteBgImageByUserId(currentUser);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(currentUser.getAvatarImageUrl());
    }
}


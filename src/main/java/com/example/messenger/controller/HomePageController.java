package com.example.messenger.controller;

import com.example.messenger.model.User;
import com.example.messenger.service.ImageService;
import com.example.messenger.service.UserService;
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


@Controller
public class HomePageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String getSelfHomePage(Authentication authentication, Model model){
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("avatarImageUrl", imageService.getAvatarImageUrlByUser(currentUser));
        model.addAttribute("user", currentUser);
        model.addAttribute("profileBgImageUrl", imageService.getProfileBgImageUrlByUser(currentUser));

        return "selfHomePage";
    }

    @GetMapping("/home/{userId}")
    public String getHomePage(@PathVariable("userId") Long userId, Authentication authentication, Model model) {
        User currentUser = (authentication != null)? (User) authentication.getPrincipal() : null;
        User secondUser = userService.findUserByUserId(userId);

        if (currentUser != null && Objects.equals(currentUser.getUserId(), secondUser.getUserId())){
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/image")
    public ResponseEntity<String> deleteUserImage(@RequestParam("typeImage") String typeImage, Authentication authentication){
        User currentUser = (User) authentication.getPrincipal();
        if (typeImage.equals("avatar")){
            imageService.deleteAvatarImageByUser(currentUser);
        }else if (typeImage.equals("bgImage")){
            imageService.deleteBgImageByUserId(currentUser);
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(currentUser.getAvatarImageUrl());
    }
}

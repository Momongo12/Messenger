package com.example.messenger.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Controller
public class HomePageController {

    @GetMapping("/home")
    public String getHomePage(){
        return "selfHomePage";
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
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String imageName = StringUtils.cleanPath(file.getOriginalFilename());
            Path imagePath = Paths.get("src/main/resources/static/images/" + imageName);
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            String baseUrl = request.getServletContext().getContextPath();
            String imageUrl = baseUrl + "/images/" + imageName;
            System.out.println(baseUrl + "/images/" + imageName);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

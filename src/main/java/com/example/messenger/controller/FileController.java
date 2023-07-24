package com.example.messenger.controller;


import com.example.messenger.service.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.InputStream;

@Controller
@Log4j2
public class FileController {

    @Autowired
    private ImageService imageService;

    @GetMapping(value = {"data/images/{imageName}", "data/images/defaultImages/{imageName}"})
    public void downloadImage(@PathVariable("imageName") String imageName,
                              HttpServletResponse response){
        if (imageName.startsWith("default")) {
            imageName = "defaultImages/" + imageName;
        }
        try (InputStream resource = imageService.getUserImage(imageName)) {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            StreamUtils.copy(resource, response.getOutputStream());
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

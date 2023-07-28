package com.example.messenger.service.impl;


import com.example.messenger.model.User;
import com.example.messenger.model.UserImages;
import com.example.messenger.repository.UserImagesRepository;
import com.example.messenger.service.ImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * The ImageServiceImpl class implements the {@link ImageService} interface and provides methods for managing user images.
 *
 * @version : 1.0
 * @author : Denis Moskvin
 */
@Service
@Log4j2
public class ImageServiceImpl implements ImageService {

    @Autowired
    private UserImagesRepository userImagesRepository;
    private static String pathToImagesFolder;

    public ImageServiceImpl() {
        if (System.getenv("PATH_TO_IMAGES_FOLDER") != null) {
            pathToImagesFolder = System.getenv("PATH_TO_IMAGES_FOLDER");
        }else {
            pathToImagesFolder = "data/images/";
        }
    }

    public String getAvatarImageUrlByUser(User user) {
        UserImages userImages = userImagesRepository.findByUser(user).orElse(new UserImages(user));

        if (userImages.getAvatarImageName() != null){
            return userImages.getAvatarImageName();
        }else if (userImages.getDefaultAvatarImageUrl() != null) {
            return userImages.getDefaultAvatarImageUrl();
        }else {
            File directory = new File(pathToImagesFolder + "defaultImages/");
            int filesNumber = directory.listFiles((dir, name) -> name.startsWith("defaultAvatar") && name.endsWith(".jpg")).length;
            int randomFileNumber = new Random().nextInt(filesNumber) + 1;
            String defaultAvatarImageUrl = "data/images/defaultImages/" + "defaultAvatar" + randomFileNumber + ".jpg";
            userImages.setDefaultAvatarImageUrl(defaultAvatarImageUrl);
            userImagesRepository.save(userImages);

            return defaultAvatarImageUrl;

        }
    }

    public String getProfileBgImageUrlByUser(User user){
        UserImages userImages = userImagesRepository.findByUser(user).orElse(new UserImages(user));

        if (userImages.getProfileBgImageName() != null){
            return userImages.getProfileBgImageName();
        }else if (userImages.getDefaultProfileBgImageUrl() != null){
            return userImages.getDefaultProfileBgImageUrl();
        }else {
            File directory = new File(pathToImagesFolder + "defaultImages/");
            int filesNumber = directory.listFiles((dir, name) -> name.startsWith("defaultProfileBg") && name.endsWith(".jpg")).length;
            int randomFileNumber = new Random().nextInt(filesNumber) + 1;
            String defaultProfileBgImageUrl = "data/images/defaultImages/" + "defaultProfileBg" + randomFileNumber + ".jpg";
            userImages.setDefaultProfileBgImageUrl(defaultProfileBgImageUrl);
            userImagesRepository.save(userImages);

            return defaultProfileBgImageUrl;

        }
    }

    public String updateUserAvatarImageUrlByUser(User user, MultipartFile image) throws IOException {
        Optional<UserImages> userImagesOptional = userImagesRepository.findByUser(user);
        String imageName;
        if (image.getOriginalFilename() != null){
            String originalImageName = image.getOriginalFilename();
            imageName = UUID.randomUUID() + originalImageName.substring(originalImageName.lastIndexOf("."));
        }else {
            imageName = UUID.randomUUID() + ".png";
        }
        String imageUrl = "data/images/" + imageName;

        saveImageToFolder(image, imageName);
        UserImages userImages;

        if (userImagesOptional.isPresent()){
            userImages = userImagesOptional.get();
            userImages.setAvatarImageName(imageUrl);
        }else {
            userImages = new UserImages();
            userImages.setUser(user);
            userImages.setAvatarImageName(imageUrl);
        }

        user.setUserImages(userImages);
        userImagesRepository.save(userImages);

        return pathToImagesFolder + imageName;
    }

    public String updateUserBgImageUrlByUser(User user, MultipartFile image) throws IOException {
        Optional<UserImages> userImagesOptional = userImagesRepository.findByUser(user);

        String imageName;
        if (image.getOriginalFilename() != null){
            String originalImageName = image.getOriginalFilename();
            imageName = UUID.randomUUID() + originalImageName.substring(originalImageName.lastIndexOf("."));
        }else {
            imageName = UUID.randomUUID() + ".png";
        }
        String imageUrl = "data/images/" + imageName;

        saveImageToFolder(image, imageName);
        UserImages userImages;
        if (userImagesOptional.isPresent()){
            userImages = userImagesOptional.get();
            userImages.setProfileBgImageName(imageUrl);
        }else {
            userImages = new UserImages();
            userImages.setUser(user);
            userImages.setProfileBgImageName(imageUrl);
        }

        user.setUserImages(userImages);
        userImagesRepository.save(userImages);

        return pathToImagesFolder + imageUrl;
    }


    public void deleteAvatarImageByUser(User user) {
        String imageUrl = user.getUserImages().getAvatarImageName();
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        try {
            deleteImageFromFolder(imageName);
        }catch (IOException e){
            log.error(e.getMessage());
        }

        userImagesRepository.deleteAvatarImageNameByUserId(user.getUserId());
        user.getUserImages().setAvatarImageName(null);
    }


    public void deleteBgImageByUserId(User user) {
        String imageUrl = user.getUserImages().getProfileBgImageName();
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        try {
            deleteImageFromFolder(imageName);
        }catch (IOException e){
            log.error(e.getMessage());
        }

        userImagesRepository.deleteBgImageNameByUserId(user.getUserId());
        user.getUserImages().setProfileBgImageName(null);
    }

    public InputStream getUserImage(String imageName) throws IOException {
        return new FileInputStream(pathToImagesFolder + imageName);
    }

    private void saveImageToFolder(MultipartFile image, String imageName) throws IOException {
        Path imagePath = Paths.get(pathToImagesFolder + imageName);
        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void deleteImageFromFolder(String imageName) throws IOException {
        Path imagePath = Paths.get(pathToImagesFolder + imageName);
        Files.delete(imagePath);
    }
}

package com.example.messenger.service;


import com.example.messenger.model.User;
import com.example.messenger.model.UserImages;
import com.example.messenger.repository.UserImagesRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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

    @Autowired
    private final ResourceLoader resourceLoader;
    private static String pathToImagesFolder;

    public ImageServiceImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

        if (System.getenv("PATH_TO_IMAGES_FOLDER") != null) {
            pathToImagesFolder = System.getenv("PATH_TO_IMAGES_FOLDER");
        }else {
            pathToImagesFolder = "data/images/";
        }
    }

    public String getAvatarImageUrlByUser(User user) {
        UserImages userImages = userImagesRepository.findByUser(user).orElse(new UserImages(user));

        if (userImages.getAvatarImageName() != null){
            return "/data/images/" + userImages.getAvatarImageName();
        }else if (userImages.getDefaultAvatarImageUrl() != null) {
            return userImages.getDefaultAvatarImageUrl();
        }else {
            try {
                Resource resource = resourceLoader.getResource("classpath:static/images/defaultImages");
                File directory = resource.getFile();
                int filesNumber = directory.listFiles((dir, name) -> name.startsWith("defaultAvatar") && name.endsWith(".jpg")).length;
                int randomFileNumber = new Random().nextInt(filesNumber) + 1;
                String defaultAvatarImageUrl = "/images/defaultImages/" + "defaultAvatar" + randomFileNumber + ".jpg";
                userImages.setDefaultAvatarImageUrl(defaultAvatarImageUrl);
                userImagesRepository.save(userImages);

                return defaultAvatarImageUrl;
            }catch (IOException e) {
                log.error(e.getMessage());
            }

            return null;
        }
    }

    public String getProfileBgImageUrlByUser(User user){
        UserImages userImages = userImagesRepository.findByUser(user).orElse(new UserImages(user));

        if (userImages.getProfileBgImageName() != null){
            return "/data/images/" + userImages.getProfileBgImageName();
        }else if (userImages.getDefaultProfileBgImageUrl() != null){
            return userImages.getDefaultProfileBgImageUrl();
        }else {
            try {
                Resource resource = resourceLoader.getResource("classpath:static/images/defaultImages");
                File directory = resource.getFile();
                int filesNumber = directory.listFiles((dir, name) -> name.startsWith("defaultProfileBg") && name.endsWith(".jpg")).length;
                int randomFileNumber = new Random().nextInt(filesNumber) + 1;
                String defaultProfileBgImageUrl = "/images/defaultImages/" + "defaultProfileBg" + randomFileNumber + ".jpg";
                userImages.setDefaultProfileBgImageUrl(defaultProfileBgImageUrl);
                userImagesRepository.save(userImages);

                return defaultProfileBgImageUrl;
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            return null;
        }
    }

    public String updateUserAvatarImageUrlByUser(User user, MultipartFile image) throws IOException {
        Optional<UserImages> userImagesOptional = userImagesRepository.findByUser(user);
        String imageName = saveImageToFolder(image);
        UserImages userImages;

        if (userImagesOptional.isPresent()){
            userImages = userImagesOptional.get();
            userImages.setAvatarImageName(imageName);
        }else {
            userImages = new UserImages();
            userImages.setUser(user);
            userImages.setAvatarImageName(imageName);
        }

        user.setUserImages(userImages);
        userImagesRepository.save(userImages);

        return pathToImagesFolder + imageName;
    }

    public String updateUserBgImageUrlByUser(User user, MultipartFile image) throws IOException {
        Optional<UserImages> userImagesOptional = userImagesRepository.findByUser(user);
        String imageUrl = saveImageToFolder(image);
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

    private String saveImageToFolder(MultipartFile image) throws IOException {
        String imageName;
        if (image.getOriginalFilename() != null){
            imageName = StringUtils.cleanPath(image.getOriginalFilename());
        }else {
            imageName = UUID.randomUUID() + ".png";
        }
        Path imagePath = Paths.get(pathToImagesFolder + imageName);
        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

        return imageName;
    }

    private void deleteImageFromFolder(String imageName) throws IOException {
        Path imagePath = Paths.get(pathToImagesFolder + imageName);
        Files.delete(imagePath);
    }
}

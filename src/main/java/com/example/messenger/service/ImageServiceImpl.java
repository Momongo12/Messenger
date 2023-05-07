package com.example.messenger.service;


import com.example.messenger.model.User;
import com.example.messenger.model.UserImages;
import com.example.messenger.repository.UserImagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService{

    @Autowired
    private UserImagesRepository userImagesRepository;

    public String getAvatarimageUrlByUserId(Long userId) {
        return null;
    }

    public String getBgimageUrlByUserId(Long userId) {
        return null;
    }

    public String updateUserAvatarImageUrlByUserId(User user, MultipartFile image) throws IOException {
        Optional<UserImages> userImagesOptional = userImagesRepository.findByUser(user);
        String imageUrl = saveImageToFolder(image);
        UserImages userImages;

        if (userImagesOptional.isPresent()){
            userImages = userImagesOptional.get();
            userImages.setAvatarImageUrl(imageUrl);
        }else {
            userImages = new UserImages();
            userImages.setUser(user);
            userImages.setAvatarImageUrl(imageUrl);
        }
        user.setUserImages(userImages);
        userImagesRepository.save(userImages);
        return imageUrl;
    }

    public String updateUserBgimageUrlByUserID(Long userId, MultipartFile image) {
        return null;
    }


    public void deleteAvatarImageByUser(User user) {
        String imageUrl = user.getUserImages().getAvatarImageUrl();
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        try {
            deleteImageFromFolder(imageName);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        userImagesRepository.deleteAvatarImageUrlByUserId(user.getUserId());
        user.getUserImages().setAvatarImageUrl(null);
    }


    public void deleteBgImageByUserId(User user) {
        String imageUrl = user.getUserImages().getAvatarImageUrl();
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        try {
            deleteImageFromFolder(imageName);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        userImagesRepository.deleteBgImageUrlByUserId(user.getUserId());
        user.getUserImages().setProfileBgImageUrl(null);
    }

    private String saveImageToFolder(MultipartFile image) throws IOException {
        String imageName;
        if (image.getOriginalFilename() != null){
            imageName = StringUtils.cleanPath(image.getOriginalFilename());
        }else {
            imageName = UUID.randomUUID() + ".png";
        }
        Path imagePath = Paths.get("target/classes/static/images/" + imageName);
        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + imageName;
    }

    private void deleteImageFromFolder(String imageName) throws IOException {
        Path imagePath = Paths.get("target/classes/static/images/" + imageName);
        Files.delete(imagePath);
    }
}

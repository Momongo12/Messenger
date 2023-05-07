package com.example.messenger.service;

import com.example.messenger.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String getAvatarimageUrlByUserId(Long userId);

    String getBgimageUrlByUserId(Long userId);

    String updateUserAvatarImageUrlByUserId(User user, MultipartFile image) throws IOException;

    String updateUserBgimageUrlByUserID(Long userId, MultipartFile image);

    void deleteAvatarImageByUser(User user);

    void deleteBgImageByUserId(User user);
}

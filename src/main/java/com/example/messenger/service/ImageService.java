package com.example.messenger.service;

import com.example.messenger.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String getAvatarImageUrlByUser(User user);

    String getProfileBgImageUrlByUser(User user);

    String updateUserAvatarImageUrlByUser(User user, MultipartFile image) throws IOException;

    String updateUserBgImageUrlByUser(User user, MultipartFile image) throws IOException;

    void deleteAvatarImageByUser(User user);


    void deleteBgImageByUserId(User user);
}

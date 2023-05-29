package com.example.messenger.service;

import com.example.messenger.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * The ImageService interface provides methods for managing user images.
 *
 * @version: 1.0
 * @author : Denis Moskvin
 */
public interface ImageService {

    /**
     * Retrieves the avatar image URL for a given user.
     *
     * @param user The User object.
     * @return The URL of the avatar image.
     */
    String getAvatarImageUrlByUser(User user);

    /**
     * Retrieves the profile background image URL for a given user.
     *
     * @param user The User object.
     * @return The URL of the profile background image.
     */
    String getProfileBgImageUrlByUser(User user);

    /**
     * Updates the user's avatar image URL.
     *
     * @param user  The User object.
     * @param image The new avatar image file.
     * @return The updated avatar image URL.
     * @throws IOException if an I/O error occurs while updating the image.
     */
    String updateUserAvatarImageUrlByUser(User user, MultipartFile image) throws IOException;

    /**
     * Updates the user's profile background image URL.
     *
     * @param user  The User object.
     * @param image The new profile background image file.
     * @return The updated profile background image URL.
     * @throws IOException if an I/O error occurs while updating the image.
     */
    String updateUserBgImageUrlByUser(User user, MultipartFile image) throws IOException;

    /**
     * Deletes the avatar image of a given user.
     *
     * @param user The User object.
     */
    void deleteAvatarImageByUser(User user);

    /**
     * Deletes the profile background image of a given user.
     *
     * @param user The User object.
     */
    void deleteBgImageByUserId(User user);
}


package com.example.messenger.service;

import com.example.messenger.model.User;
import com.example.messenger.model.UserImages;
import com.example.messenger.repository.UserImagesRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {
    @Mock
    private UserImagesRepository userImagesRepository;

    @InjectMocks
    private ImageServiceImpl imageService;

    @Captor
    private ArgumentCaptor<UserImages> userImagesCaptor;

    @Test
    void getAvatarImageUrlByUser_existingAvatarImageUrl_returnsAvatarImageUrl() {
        User user = new User();
        user.setUserId(1L);
        UserImages userImages = new UserImages(user);
        userImages.setAvatarImageUrl("avatar.jpg");
        when(userImagesRepository.findByUser(user)).thenReturn(Optional.of(userImages));

        String imageUrl = imageService.getAvatarImageUrlByUser(user);

        assertEquals("avatar.jpg", imageUrl);
    }

    @Test
    void getAvatarImageUrlByUser_existingDefaultAvatarImageUrl_returnsDefaultAvatarImageUrl() {
        User user = new User();
        user.setUserId(1L);
        UserImages userImages = new UserImages(user);
        userImages.setDefaultAvatarImageUrl("defaultAvatar.jpg");
        when(userImagesRepository.findByUser(user)).thenReturn(Optional.of(userImages));

        String imageUrl = imageService.getAvatarImageUrlByUser(user);

        assertEquals("defaultAvatar.jpg", imageUrl);
    }

    @Test
    void getAvatarImageUrlByUser_noAvatarImageUrls_createsDefaultAvatarImageUrl() {
        User user = new User();
        user.setUserId(1L);
        when(userImagesRepository.findByUser(user)).thenReturn(Optional.empty());

        String imageUrl = imageService.getAvatarImageUrlByUser(user);

        assertTrue(imageUrl.startsWith("/images/defaultImages/defaultAvatar"));
        assertTrue(imageUrl.endsWith(".jpg"));

        verify(userImagesRepository).save(any(UserImages.class));
    }

    @Test
    void getProfileBgImageUrlByUser_existingProfileBgImageUrl_returnProfileBgImageUrl() {
        User user = new User();
        user.setUserId(1L);
        UserImages userImages = new UserImages(user);
        userImages.setProfileBgImageUrl("profileBgImageUrl.jpg");
        when(userImagesRepository.findByUser(user)).thenReturn(Optional.of(userImages));

        String imageUrl = imageService.getProfileBgImageUrlByUser(user);

        assertEquals(imageUrl, "profileBgImageUrl.jpg");
    }

    @Test
    void getProfileBgImageUrlByUser_existingDefaultProfileBgImageUrl_returnDefaultProfileBgImageUrl() {
        User user = new User();
        user.setUserId(1L);
        UserImages userImages = new UserImages(user);
        userImages.setDefaultProfileBgImageUrl("defaultProfileBgImageUrl.jpg");
        when(userImagesRepository.findByUser(user)).thenReturn(Optional.of(userImages));

        String imageUrl = imageService.getProfileBgImageUrlByUser(user);

        assertEquals(imageUrl, "defaultProfileBgImageUrl.jpg");
    }

    @Test
    void getProfileBgImageUrlByUser_noProfileBgImageUrls_createsDefaultProfileBgImageUrl() {
        User user = new User();
        user.setUserId(1L);
        when(userImagesRepository.findByUser(user)).thenReturn(Optional.empty());

        String imageUrl = imageService.getProfileBgImageUrlByUser(user);

        assertTrue(imageUrl.startsWith("/images/defaultImages/defaultProfileBg"));
        assertTrue(imageUrl.endsWith(".jpg"));

        verify(userImagesRepository).save(any(UserImages.class));
    }

    @Test
    void updateUserAvatarImageUrlByUser_existingUserImagesWithAvatarImageUrl_shouldUpdateUserImagesWithAvatarImageUrl() throws IOException {
        User user = new User();
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]);
        String expectedImageUrl = "/images/image.jpg";
        UserImages existingUserImages = new UserImages();
        existingUserImages.setUser(user);
        existingUserImages.setAvatarImageUrl("/images/old_image.jpg");
        Optional<UserImages> userImagesOptional = Optional.of(existingUserImages);
        when(userImagesRepository.findByUser(user)).thenReturn(userImagesOptional);

        String result = imageService.updateUserAvatarImageUrlByUser(user, image);

        assertEquals(expectedImageUrl, result);
        verify(userImagesRepository).save(userImagesCaptor.capture());
        UserImages savedUserImages = userImagesCaptor.getValue();
        assertEquals(user, savedUserImages.getUser());
        assertEquals(expectedImageUrl, savedUserImages.getAvatarImageUrl());
    }

    @Test
    void updateUserAvatarImageUrlByUser_newUserImages_shouldCreateUserImagesWithAvatarImageUrl() throws IOException {
        User user = new User();
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]);
        String expectedImageUrl = "/images/image.jpg";
        Optional<UserImages> userImagesOptional = Optional.empty();
        when(userImagesRepository.findByUser(user)).thenReturn(userImagesOptional);

        String result = imageService.updateUserAvatarImageUrlByUser(user, image);

        assertEquals(expectedImageUrl, result);
        verify(userImagesRepository).save(userImagesCaptor.capture());
        UserImages savedUserImages = userImagesCaptor.getValue();
        assertEquals(user, savedUserImages.getUser());
        assertEquals(expectedImageUrl, savedUserImages.getAvatarImageUrl());
    }

    @Test
    void updateUserBgImageUrlByUser_existingUserImagesWithProfileBgImageUrl_shouldUpdateUserImagesWithProfileBgImageUrl() throws IOException {
        User user = new User();
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]);
        String expectedImageUrl = "/images/image.jpg";
        UserImages existingUserImages = new UserImages();
        existingUserImages.setUser(user);
        existingUserImages.setProfileBgImageUrl("/images/old_image.jpg");
        Optional<UserImages> userImagesOptional = Optional.of(existingUserImages);
        when(userImagesRepository.findByUser(user)).thenReturn(userImagesOptional);

        String result = imageService.updateUserBgImageUrlByUser(user, image);

        assertEquals(expectedImageUrl, result);
        verify(userImagesRepository).save(userImagesCaptor.capture());
        UserImages savedUserImages = userImagesCaptor.getValue();
        assertEquals(user, savedUserImages.getUser());
        assertEquals(expectedImageUrl, savedUserImages.getProfileBgImageUrl());
    }

    @Test
    void updateUserBgImageUrlByUser_newUserImages_shouldCreateUserImagesWithProfileBgImageUrl() throws IOException {
        User user = new User();
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]);
        String expectedImageUrl = "/images/image.jpg";
        Optional<UserImages> userImagesOptional = Optional.empty();
        when(userImagesRepository.findByUser(user)).thenReturn(userImagesOptional);

        String result = imageService.updateUserBgImageUrlByUser(user, image);

        assertEquals(expectedImageUrl, result);
        verify(userImagesRepository).save(userImagesCaptor.capture());
        UserImages savedUserImages = userImagesCaptor.getValue();
        assertEquals(user, savedUserImages.getUser());
        assertEquals(expectedImageUrl, savedUserImages.getProfileBgImageUrl());
    }

    @Test
    void deleteAvatarImageByUser_existingAvatarImageUrl_deletesImageAndResetsAvatarImageUrl(){
        User user = new User();
        user.setUserId(1L);
        UserImages userImages = new UserImages(user);
        userImages.setAvatarImageUrl("/images/avatar.jpg");
        user.setUserImages(userImages);

        imageService.deleteAvatarImageByUser(user);

        verify(userImagesRepository).deleteAvatarImageUrlByUserId(user.getUserId());
        assertNull(user.getUserImages().getAvatarImageUrl());
    }

    @Test
    void deleteBgImageByUserId_existingBgImageUrl_deletesImageAndResetsBgImageUrl(){
        User user = new User();
        user.setUserId(1L);
        UserImages userImages = new UserImages(user);
        userImages.setProfileBgImageUrl("/images/bgimage.jpg");
        user.setUserImages(userImages);

        imageService.deleteBgImageByUserId(user);

        verify(userImagesRepository).deleteBgImageUrlByUserId(user.getUserId());
        assertNull(user.getUserImages().getProfileBgImageUrl());
    }
}
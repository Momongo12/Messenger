package com.example.messenger.repository;

import com.example.messenger.model.User;
import com.example.messenger.model.UserImages;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserImagesRepository extends JpaRepository<UserImages, Long> {
    Optional<UserImages> findByUser(User user);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_images SET avatar_image_url=null WHERE user_id=:userId", nativeQuery = true)
    int deleteAvatarImageUrlByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "UPDATE user_images SET profile_bg_image_url=null WHERE user_id=:userId", nativeQuery = true)
    int deleteBgImageUrlByUserId(@Param("userId") Long userId);
}

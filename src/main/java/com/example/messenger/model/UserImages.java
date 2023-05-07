package com.example.messenger.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_images")
@Data
public class UserImages {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "avatar_image_url")
    private String AvatarImageUrl;

    @Column(name = "profile_bg_image_url")
    private String profileBgImageUrl;

    public UserImages(){

    }
}

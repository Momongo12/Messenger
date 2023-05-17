package com.example.messenger.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users_details")
public class MyUserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long usersDetailsId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "short_info")
    private String shortInfo;

    @Column(name = "public_profile_flag")
    private boolean publicProfileFlag;

    @Column(name = "showing_email_flag")
    private boolean showingEmailFlag;

    public MyUserDetails() {

    }
}

package com.example.messenger.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserInfoDto {
    private String username;
    private String userDescription;
    private String email;
    private String uniqueUsername;
    private String phoneNumber;
    private Boolean isPublicProfile;
    private Boolean isShowingEmail;
}
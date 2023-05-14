package com.example.messenger.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserInfoDto {
    private String userName;
    private String userDescription;
}
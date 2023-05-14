package com.example.messenger.service;


import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User findByEmail(String email);

    User findUserByUserId(Long userId);
    boolean saveUser(User user);

    List<User> getUsersByUnigueUsernamePrefix(String usernamePrefix);

    void updateUserInfo(UserInfoDto userInfoDto, User user);

    User getUserByUniqueUsername(String uniqueUsername);

    boolean isUserOnline(String username);
}
package com.example.messenger.service;


import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Chat;
import com.example.messenger.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User findByEmail(String email);

    User findUserByUserId(Long userId);
    boolean createUser(User user);

    void createChat(Chat chat, User firstUser, String secondUserUniqueUsername) throws IllegalArgumentException;

    List<User> getUsersByUnigueUsernamePrefix(String usernamePrefix);

    void updateUserDetails(UserInfoDto userInfoDto, User user) throws IllegalArgumentException;

    User getUserByUniqueUsername(String uniqueUsername);

    boolean isUserOnline(String username);
}
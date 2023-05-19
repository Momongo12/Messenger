package com.example.messenger.service;


import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Chat;
import com.example.messenger.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {
    User findByEmail(String email);

    User findUserByUserId(Long userId);
    boolean createUser(User user);

    List<Map<String, Object>> getUsersMapsListByUniqueUsernamePrefixWithoutExistChats(String usernamePrefix,
                                                                                          List<Chat> chats, User currentUser);

    void updateUserDetails(UserInfoDto userInfoDto, User user) throws IllegalArgumentException;

    User getUserByUniqueUsername(String uniqueUsername);

    boolean isUserOnline(String username);
}
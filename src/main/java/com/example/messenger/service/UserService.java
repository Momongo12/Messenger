package com.example.messenger.service;


import com.example.messenger.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    public User findByEmail(String email);

    User findUserByUserId(Long userId);
    public boolean saveUser(User user);

    public List<User> getUsersByUnigueUsernamePrefix(String usernamePrefix);

    User getUserByUniqueUsername(String uniqueUsername);
}
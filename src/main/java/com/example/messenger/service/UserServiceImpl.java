package com.example.messenger.service;


import com.example.messenger.model.Role;
import com.example.messenger.model.User;
import com.example.messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service("UserServiceImpl")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public boolean saveUser(User user) {
        User userFromDb = userRepository.findByEmail(user.getEmail());

        if (userFromDb != null){
            return false;
        }

        user.setRoles(Collections.singleton(new Role("ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return true;
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserByUserId(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsersByUnigueUsernamePrefix(String usernamePrefix) {
        return userRepository.findByUniqueUsernameStartingWith(usernamePrefix);
    }

    @Override
    public User getUserByUniqueUsername(String uniqueUsername) {
        return userRepository.findByUniqueUsername(uniqueUsername);
    }
}
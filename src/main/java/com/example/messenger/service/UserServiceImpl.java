package com.example.messenger.service;


import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Role;
import com.example.messenger.model.User;
import com.example.messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
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

    @Autowired
    private SessionRegistry sessionRegistry;

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

    public User findUserByUserId(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByUnigueUsernamePrefix(String usernamePrefix) {
        return userRepository.findByUniqueUsernameStartingWith(usernamePrefix);
    }

    public void updateUserInfo(UserInfoDto userInfoDto, User user){
        if (userInfoDto.getUserName() != null) {
            user.setUsername(userInfoDto.getUserName());
        }
        if (userInfoDto.getUserDescription() !=null) {
            user.setShortInfo(userInfoDto.getUserDescription());
        }
        userRepository.save(user);
    }

    public User getUserByUniqueUsername(String uniqueUsername) {
        return userRepository.findByUniqueUsername(uniqueUsername);
    }

    public boolean isUserOnline(String username) {
        List<Object> principals = sessionRegistry.getAllPrincipals();
        for (Object principal : principals) {
            if (principal instanceof User) {
                User user = (User) principal;
                if (user.getUsername().equals(username)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                    System.out.println(sessions);
                    if (!sessions.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
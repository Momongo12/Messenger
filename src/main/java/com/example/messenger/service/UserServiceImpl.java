package com.example.messenger.service;


import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Chat;
import com.example.messenger.model.MyUserDetails;
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

import java.util.ArrayList;
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

    @Autowired
    private ChatService chatService;

    @Transactional
    public boolean createUser(User user) {
        User userFromDb = userRepository.findByEmail(user.getEmail());

        if (userFromDb != null){
            return false;
        }

        user.setRoles(Collections.singleton(new Role("ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        MyUserDetails userDetails = new MyUserDetails();
        userDetails.setUser(user);
        user.setUserDetails(userDetails);

        userRepository.save(user);

        return true;
    }

    @Transactional
    public void createChat(Chat chat, User firstUser, String secondUserUniqueUsername) throws IllegalArgumentException {
        User secondUser = getUserByUniqueUsername(secondUserUniqueUsername);

        if (secondUser == null) throw new IllegalArgumentException();

        List<User> usersList = new ArrayList<>();
        usersList.add(firstUser);
        usersList.add(secondUser);

        chat.setMembers(usersList);
        chatService.saveChat(chat);
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

    public void updateUserDetails(UserInfoDto userInfoDto, User user) throws IllegalArgumentException{
        User userWithNewUniqueUsername = userRepository.findByUniqueUsername(userInfoDto.getUniqueUsername());

        if (userWithNewUniqueUsername != null && !user.equals(userWithNewUniqueUsername)) {
            throw new IllegalArgumentException();
        }else {
            user.setUniqueUsername(userInfoDto.getUniqueUsername());
        }

        if (userInfoDto.getUsername() != null) {
            user.setUsername(userInfoDto.getUsername());
        }
        if (userInfoDto.getUserDescription() != null) {
            user.getUserDetails().setShortInfo(userInfoDto.getUserDescription());
        }
        if (userInfoDto.getEmail() != null) {
            user.setEmail(userInfoDto.getEmail());
        }
        if (userInfoDto.getPhoneNumber() != null) {
            user.getUserDetails().setPhoneNumber(userInfoDto.getPhoneNumber());
        }

        user.getUserDetails().setPublicProfileFlag(userInfoDto.getIsPublicProfile());
        user.getUserDetails().setShowingEmailFlag(userInfoDto.getIsShowingEmail());

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
                    if (!sessions.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
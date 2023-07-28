package com.example.messenger.service.impl;


import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Chat;
import com.example.messenger.model.MyUserDetails;
import com.example.messenger.model.Role;
import com.example.messenger.model.User;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.service.ImageService;
import com.example.messenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * The UserServiceImpl class provides an implementation of the {@link UserService} interface.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@Service("UserServiceImpl")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SessionRegistry sessionRegistry;

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

    public List<Map<String, Object>> getUsersMapsListByUniqueUsernamePrefixWithoutExistChats(String usernamePrefix, List<Chat> chats, User currentUser) {
        List<User> users = userRepository.findByUniqueUsernameStartingWith(usernamePrefix);
        List<Map<String, Object>> usersList = new ArrayList<>();

        for (User user : users) {
            if (user.equals(currentUser) || !user.getUserDetails().isPublicProfileFlag()) continue;
            boolean chatWithThisUserExist = false;
            for (Chat chat : chats) {
                if (chat.getInterlocutorId(currentUser).equals(user.getUserId())) {
                    chatWithThisUserExist = true;
                    break;
                }
            }
            if (!chatWithThisUserExist) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("chatId", 0);
                userMap.put("username", user.getUsername());
                userMap.put("uniqueUsername", user.getUniqueUsername());
                userMap.put("chatAvatarImageUrl", imageService.getAvatarImageUrlByUser(user));
                usersList.add(userMap);
            }
        }

        return usersList;
    }

    public void updateUserDetails(UserInfoDto userInfoDto, User user) throws IllegalArgumentException{

        if (userInfoDto.getUsername() != null) {
            user.setUsername(userInfoDto.getUsername());
        }
        if (userInfoDto.getUniqueUsername() != null) {
            User userWithNewUniqueUsername = userRepository.findByUniqueUsername(userInfoDto.getUniqueUsername());

            if (userWithNewUniqueUsername != null && !user.equals(userWithNewUniqueUsername)) {
                throw new IllegalArgumentException();
            }else {
                user.setUniqueUsername(userInfoDto.getUniqueUsername());
            }
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
        if (userInfoDto.getIsPublicProfile() != null) {
            user.getUserDetails().setPublicProfileFlag(userInfoDto.getIsPublicProfile());
        }
        if (userInfoDto.getIsShowingEmail() != null) {
            user.getUserDetails().setShowingEmailFlag(userInfoDto.getIsShowingEmail());
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
                    if (!sessions.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
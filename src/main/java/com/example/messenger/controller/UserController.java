package com.example.messenger.controller;

import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Chat;
import com.example.messenger.model.User;
import com.example.messenger.service.ChatService;
import com.example.messenger.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * This is the UserController class that handles user-related API endpoints.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@RestController
@Log4j2
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    /**
     * Retrieves the list of chats for the authenticated user.
     *
     * @param authentication  the authentication object containing the current user's information
     * @return a map containing the list of chats
     */
    @GetMapping("/api/chats")
    public Map<String, Object> getChatsUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = userService.findUserByUserId(((User) authentication.getPrincipal()).getUserId());

        List<Chat> chats = currentUser.getChats();
        List<Map<String, Object>> chatsList = chatService.convertChatsListToChatsMapList(chats, currentUser);
        response.put("chatsList", chatsList);

        return response;
    }

    /**
     * Updates the user's information.
     *
     * @param userInfoDto     the DTO object containing the updated user information
     * @param authentication  the authentication object containing the current user's information
     */
    @PostMapping("/user/info")
    public void updateUserInfo(@RequestBody UserInfoDto userInfoDto, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        userService.updateUserDetails(userInfoDto, currentUser);
    }

    /**
     * Retrieves the list of chats for the authenticated user and the found users based on the provided username prefix.
     *
     * @param usernamePrefix   the prefix of the username to search for
     * @param authentication  the authentication object containing the current user's information
     * @return a map containing the list of chats and the list of found users
     */
    @GetMapping("/search/users")
    public Map<String, Object> getChatsUserAndAnotherFoundUsers(@RequestParam String usernamePrefix, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();

        List<Chat> chats = currentUser.getChatsForUsernamePrefix(usernamePrefix);
        List<Map<String, Object>> chatsList = chatService.convertChatsListToChatsMapList(chats, currentUser);
        response.put("chatsList", chatsList);

        response.put("usersList", userService.getUsersMapsListByUniqueUsernamePrefixWithoutExistChats(usernamePrefix, chats, currentUser));

        return response;
    }
}
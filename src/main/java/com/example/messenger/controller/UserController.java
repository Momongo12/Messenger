package com.example.messenger.controller;

import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Chat;
import com.example.messenger.model.User;
import com.example.messenger.service.ChatService;
import com.example.messenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @GetMapping("/api/chats")
    public Map<String, Object> getChatsUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = userService.findUserByUserId(((User) authentication.getPrincipal()).getUserId());

        List<Chat> chats = currentUser.getChats();
        List<Map<String, Object>> chatsList = chatService.convertChatsListToChatsMapList(chats, currentUser);
        response.put("chatsList", chatsList);

        return response;
    }

    @PostMapping("/user/info")
    public void updateUserInfo(@RequestBody UserInfoDto userInfoDto, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        userService.updateUserDetails(userInfoDto, currentUser);
    }

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

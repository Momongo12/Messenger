package com.example.messenger.controller;

import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Chat;
import com.example.messenger.model.User;
import com.example.messenger.service.ChatService;
import com.example.messenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
        List<Map<String, Object>> chatsList = getChatsList(chats, currentUser);
        response.put("chatsList", chatsList);

        return response;
    }

    @PostMapping("/user/info")
    public void updateUserInfo(@RequestBody UserInfoDto userInfoDto, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        userService.updateUserDetails(userInfoDto, currentUser);
    }

    @PostMapping("api/chats/{secondInterlocutorUniqueUsername}")
    public ResponseEntity<Map<String, Object>> createChat(@RequestBody Chat chat, @PathVariable String secondInterlocutorUniqueUsername,
                                                     Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();

        try {
            userService.createChat(chat, currentUser, secondInterlocutorUniqueUsername);

            List<Map<String, Object>> chatsList = getChatsList(currentUser.getChats(), currentUser);

            response.put("currentChatId", chat.getChatId());
            response.put("chatsList", chatsList);

            return ResponseEntity.ok().body(response);
        }catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/search/users")
    public Map<String, Object> getChatsUserAndAnotherFoundUsers(@RequestParam String usernamePrefix, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();

        List<Chat> chats = currentUser.getChatsForUsernamePrefix(usernamePrefix);
        List<Map<String, Object>> chatsList = getChatsList(chats, currentUser);
        response.put("chatsList", chatsList);

        response.put("usersList", getListUsersByUnigueUsernamePrefixWithoutExistChats(usernamePrefix, chats, currentUser));

        return response;
    }

    private List<Map<String, Object>> getChatsList(List<Chat> chats, User currentUser) {
        List<Map<String, Object>> chatsList = new ArrayList<>();

        for (Chat chat : chats) {
            Map<String, Object> chatMap = new HashMap<>();
            chatMap.put("chatId", chat.getChatId());
            chatMap.put("chatAvatarImageUrl", chat.getChatAvatarImageUrlForUser(currentUser));
            chatMap.put("chatName", chat.getChatName(currentUser));
            chatMap.put("lastMessage", chat.getLastMessage());
            chatMap.put("interlocutorStatus", userService.isUserOnline(chat.getChatName(currentUser)) ? "online" : "offline");

            chatsList.add(chatMap);
        }
        return chatsList;
    }

    private List<Map<String, Object>> getListUsersByUnigueUsernamePrefixWithoutExistChats(String usernamePrefix,
                                                                                          List<Chat> chats, User currentUser) {
        List<User> users = userService.getUsersByUnigueUsernamePrefix(usernamePrefix);
        List<Map<String, Object>> usersList = new ArrayList<>();

        for (User user : users) {
            if (user.equals(currentUser) || !user.getUserDetails().isPublicProfileFlag()) continue;
            boolean chatWithThisUserExist = false;
            for (Chat chat : chats) {
                if (chat.getInterLocutorId(currentUser).equals(user.getUserId())) {
                    chatWithThisUserExist = true;
                    break;
                }
            }
            if (!chatWithThisUserExist) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("chatId", 0);
                userMap.put("username", user.getUsername());
                userMap.put("uniqueUsername", user.getUniqueUsername());
                userMap.put("chatAvatarImageUrl", user.getAvatarImageUrl());
                usersList.add(userMap);
            }
        }

        return usersList;
    }
}

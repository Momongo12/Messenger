package com.example.messenger.controller;

import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Chat;
import com.example.messenger.model.User;
import com.example.messenger.service.ChatService;
import com.example.messenger.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Map<String, Object> getChatsUser(Authentication authentication){
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();

        List<Chat> chats = currentUser.getChats();
        List<Map<String, Object>> chatsList = getChatsList(chats, currentUser);
        response.put("chatsList", chatsList);

        return response;
    }

    @PostMapping("/user/info")
    public void updateUserInfo(@RequestBody UserInfoDto userInfoDto, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        userService.updateUserInfo(userInfoDto, currentUser);
    }

    @PostMapping("api/chats/{secondInterlocutorUniqueUsername}")
    public Map<String, Object> createChat(@RequestBody Chat chat, @PathVariable String secondInterlocutorUniqueUsername,
                                          Authentication authentication){
        Map<String, Object> response = new HashMap<>();

        User currentUser = (User) authentication.getPrincipal();
        User secondUser = userService.getUserByUniqueUsername(secondInterlocutorUniqueUsername);
        List<User> usersList = new ArrayList<>();
        usersList.add(currentUser);
        usersList.add(secondUser);
        chat.setMembers(usersList);

        chatService.saveChat(chat);

        List<Chat> chats = currentUser.getChats();
        chats.add(chat);
        userService.saveUser(currentUser);
        List<Map<String, Object>> chatsList = getChatsList(chats, currentUser);

        response.put("currentChatId", chat.getChatId());
        response.put("chatsList", chatsList);

        return response;
    }


    @GetMapping("/search/users")
    public Map<String, Object> getChatsUserAndAnotherFoundUsers(@RequestParam String username, Authentication authentication){
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();

        List<Chat> chats = currentUser.getChatsForUsernamePrefix(username);
        List<Map<String, Object>> chatsList = getChatsList(chats, currentUser);
        response.put("chatsList", chatsList);

        List<User> users = userService.getUsersByUnigueUsernamePrefix(username);
        List<Map<String, Object>> usersList = new ArrayList<>();

        for (User user: users){
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("chatId", 0);
            userMap.put("username", user.getUsername());
            userMap.put("uniqueUsername", user.getUniqueUsername());

            usersList.add(userMap);
        }

        response.put("usersList", usersList);

        return response;
    }

    private List<Map<String, Object>> getChatsList(List<Chat> chats, User currentUser){
        List<Map<String, Object>> chatsList = new ArrayList<>();

        for (Chat chat: chats){
            Map<String, Object> chatMap = new HashMap<>();
            chatMap.put("chatId", chat.getChatId());
            chatMap.put("chatAvatarImageUrl", chat.getChatAvatarImageUrlForUser(currentUser));
            chatMap.put("chatName", chat.getChatName(currentUser));
            chatMap.put("lastMessage", chat.getLastMessage());
            chatMap.put("interlocutorStatus", userService.isUserOnline(chat.getChatName(currentUser)) ? "online": "offline");

            chatsList.add(chatMap);
        }
        return chatsList;
    }
}

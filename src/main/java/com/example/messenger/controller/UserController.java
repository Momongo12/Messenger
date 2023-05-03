package com.example.messenger.controller;

import com.example.messenger.model.Chat;
import com.example.messenger.model.User;
import com.example.messenger.service.ChatService;
import com.example.messenger.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @GetMapping("/api/chats")
    @ResponseBody
    public Map<String, Object> getChatsUser(Authentication authentication){
        Map<String, Object> response = new HashMap<>();
        User user = (User) authentication.getPrincipal();
        List<Chat> chats = user.getChats();
        List<Map<String, Object>> chatsList = new ArrayList<>();

        for (Chat chat: chats){
            Map<String, Object> chatsMap = new HashMap<>();
            chatsMap.put("chatName", chat.getChatName(user));
            chatsMap.put("lastMessage", chat.getLastMessage());
            chatsMap.put("chatId", chat.getChatId());

            chatsList.add(chatsMap);
        }
        response.put("chatsList", chatsList);

        return response;
    }

    @PostMapping("api/chats/{secondInterlocutorUniqueUsername}")
    @ResponseBody
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
        List<Map<String, Object>> chatsList = new ArrayList<>();

        for (Chat cht: chats){
            Map<String, Object> chatMap = new HashMap<>();
            chatMap.put("chatId", cht.getChatId());
            chatMap.put("chatName", cht.getChatName(currentUser));
            chatMap.put("lastMessage", cht.getLastMessage());

            chatsList.add(chatMap);
        }

        response.put("currentChatId", chat.getChatId());
        response.put("chatsList", chatsList);

        return response;
    }

//    @PostMapping("api/chats/{secondInterlocutorUniqueUsername}")
//    @ResponseBody
//    public Map<String, Object> createChat(@RequestBody Chat chat, @PathVariable String secondInterlocutorUniqueUsername,
//                                          Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//
//        User currentUser = (User) authentication.getPrincipal();
//        User secondUser = userService.getUserByUniqueUsername(secondInterlocutorUniqueUsername);
//        List<User> usersList = new ArrayList<>();
//        usersList.add(currentUser);
//        usersList.add(entityManager.getReference(User.class, secondUser.getId()));
//
//        chat.setMembers(usersList);
//        chatService.saveChat(chat);
//
//        response.put("chatId", chat.getChatId());
////        response.put("chatsList", chatService.getChatsList(currentUser));
//
//        return response;
//    }


    @GetMapping("/search/users")
    @ResponseBody
    public Map<String, Object> getChatsUserAndAnotherFoundUsers(@RequestParam String username, Authentication authentication){
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();

        List<Chat> chats = currentUser.getChatsForUsernamePrefix(username);
        List<Map<String, Object>> chatsList = new ArrayList<>();

        for (Chat chat: chats){
            Map<String, Object> chatMap = new HashMap<>();
            chatMap.put("chatId", chat.getChatId());
            chatMap.put("chatName", chat.getChatName(currentUser));
            chatMap.put("lastMessage", chat.getLastMessage());

            chatsList.add(chatMap);
        }

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
}

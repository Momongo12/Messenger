package com.example.messenger.controller;


import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;
import com.example.messenger.model.User;
import com.example.messenger.service.ChatService;
import com.example.messenger.service.SmileysService;
import com.example.messenger.service.UserService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SmileysService smileysService;

    @Autowired
    private UserService userService;

    @GetMapping("/chats")
    public String getChats(Model model, Authentication authentication){
        User user = userService.findUserByUserId(((User) authentication.getPrincipal()).getUserId());
        List<Chat> chats = user.getChats();

        model.addAttribute("chats", chats);
        model.addAttribute("currentUser", authentication.getPrincipal());
        model.addAttribute("smileysCategoriesList", smileysService.getSmileysCategoriesList());
        model.addAttribute("displayInputField", false);

        return "chat";
    }

    @GetMapping("/chats/user={userId}")
    public String getChatByUserId(Model model, @PathVariable Long userId, Authentication authentication){
        User currentUser = (User) authentication.getPrincipal();
        List<Chat> chats = currentUser.getChats();
        Chat chat = null;

        for(Chat cht: chats) {
            if (Objects.equals(cht.getInterLocutorId(currentUser), userId)){
                chat = cht;
            }
        }

        assert chat != null;

        Hibernate.initialize(chat);

        model.addAttribute("chats", chats);
        model.addAttribute("currentUser", authentication.getPrincipal());
        model.addAttribute("chatName", chat.getChatName(currentUser));
        model.addAttribute("messages", chatService.getChatMessagesMapsList(chat.getChatId()));
        model.addAttribute("smileysCategoriesList", smileysService.getSmileysCategoriesList());
        model.addAttribute("displayInputField", true);

        return "chat";
    }

    @PostMapping("api/chats/{secondInterlocutorUniqueUsername}")
    public ResponseEntity<Map<String, Object>> createChat(@RequestBody Chat chat, @PathVariable String secondInterlocutorUniqueUsername,
                                                          Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();

        try {
            chatService.createChat(chat, currentUser, secondInterlocutorUniqueUsername);

            List<Map<String, Object>> chatsList = chatService.convertChatsListToChatsMapList(currentUser.getChats(), currentUser);

            response.put("currentChatId", chat.getChatId());
            response.put("chatsList", chatsList);

            return ResponseEntity.ok().body(response);
        }catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/chats/chat={chatId}")
    public String getChatByChatId(Model model, @PathVariable Long chatId, Authentication authentication){
        User currentUser = (User) authentication.getPrincipal();
        List<Chat> chats = currentUser.getChats();
        Chat chat = chatService.findByChatId(chatId);

        model.addAttribute("chats", chats);
        model.addAttribute("currentUser", authentication.getPrincipal());
        model.addAttribute("messages", chatService.getChatMessagesMapsList(chat.getChatId()));
        model.addAttribute("smileysCategoriesList", smileysService.getSmileysCategoriesList());
        model.addAttribute("displayInputField", true);

        return "chat";
    }

    @GetMapping("/chats/{chatId}/messages")
    @ResponseBody
    public Map<String, Object> getChatMessages(@PathVariable Long chatId) {
        Map<String, Object> response = new HashMap<>();

        response.put("messages", chatService.getChatMessagesMapsList(chatId));
        return response;
    }

    @PostMapping("/chats/messages")
    @ResponseBody
    public Map<String, Object> createMessage(@RequestBody Message message, Authentication authentication){
        chatService.updateLastMessageByChatId(message.getChatId(), message.getText());

        Map<String, Object> response = new HashMap<>();
        message.setSender((User) authentication.getPrincipal());
        message.setMessageId(null);
        boolean statusSaveMessage = chatService.saveMessage(message);

        List<Map<String, Object>> messageList = chatService.getChatMessagesMapsList(message.getChatId());

        response.put("messages", messageList);
        response.put("status", statusSaveMessage? 200 : 500);

        return response;
    }
}
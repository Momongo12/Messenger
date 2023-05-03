package com.example.messenger.controller;


import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;
import com.example.messenger.model.User;
import com.example.messenger.service.ChatService;
import com.example.messenger.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/chats")
    public String getChats(Model model, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        List<Chat> chats = user.getChats();
        model.addAttribute("chats", chats);
        model.addAttribute("currentUser", authentication.getPrincipal());

        return "chat";
    }

    @GetMapping("/chats/{chatId}/messages")
    @ResponseBody
    public Map<String, Object> getChatMessages(@PathVariable Long chatId) {
        Map<String, Object> response = new HashMap<>();

        Chat chat = chatService.findByChatId(chatId);
        if (chat == null) {
            response.put("error", "Chat not found");
            return response;
        }

        List<Message> messages = chat.getMessages();
        List<Map<String, Object>> messageList = new ArrayList<>();
        for (Message message : messages) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("text", message.getText());
            messageList.add(messageMap);
        }
        response.put("messages", messageList);

        return response;
    }

    @PostMapping("/chats/messages")
    @ResponseBody
    public Map<String, Object> createMessage(@RequestBody Message message, Authentication authentication){

        chatService.updateLastMessageByChatId(message.getChatId(), message.getText());

        Map<String, Object> response = new HashMap<>();
        message.setSenderId(((User) authentication.getPrincipal()).getId());
        message.setMessageId(null);
        boolean statusSaveMessage = chatService.saveMessage(message);

        response.put("status", statusSaveMessage? 200 : 500);


        List<Message> messages = chatService.getChatMessages(message.getChatId());
        List<Map<String, Object>> messageList = new ArrayList<>();
        for (Message msg : messages) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("text", msg.getText());
            messageList.add(messageMap);
        }
        response.put("messages", messageList);

        return response;
    }
}
package com.example.messenger.controller;


import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;
import com.example.messenger.model.User;
import com.example.messenger.service.ChatService;
import com.example.messenger.service.SmileysService;
import org.hibernate.Hibernate;
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

    @Autowired
    private SmileysService smileysService;

    @GetMapping("/chats")
    public String getChats(Model model, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        List<Chat> chats = user.getChats();
        model.addAttribute("chats", chats);
        model.addAttribute("currentUser", authentication.getPrincipal());
        model.addAttribute("smileysCategoriesList", smileysService.getSmileysCategoriesList());

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
        model.addAttribute("messages", getChatMessagesList(chat.getChatId()));
        model.addAttribute("smileysCategoriesList", smileysService.getSmileysCategoriesList());

        return "chat";
    }

    @GetMapping("/chats/chat={chatId}")
    public String getChatByChatId(Model model, @PathVariable Long chatId, Authentication authentication){
        User currentUser = (User) authentication.getPrincipal();
        List<Chat> chats = currentUser.getChats();
        Chat chat = chatService.findByChatId(chatId);

        model.addAttribute("chats", chats);
        model.addAttribute("currentUser", authentication.getPrincipal());
        model.addAttribute("messages", getChatMessagesList(chat.getChatId()));
        model.addAttribute("smileysCategoriesList", smileysService.getSmileysCategoriesList());

        return "chat";
    }

    @GetMapping("/chats/{chatId}/messages")
    @ResponseBody
    public Map<String, Object> getChatMessages(@PathVariable Long chatId) {
        Map<String, Object> response = new HashMap<>();

        response.put("messages", getChatMessagesList(chatId));
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

        response.put("status", statusSaveMessage? 200 : 500);

        List<Map<String, Object>> messageList = getChatMessagesList(message.getChatId());
        response.put("messages", messageList);

        return response;
    }

    private List<Map<String, Object>> getChatMessagesList(Long chatId){
        List<Message> messages = chatService.getChatMessages(chatId);
        List<Map<String, Object>> messageList = new ArrayList<>();
        Long lastSenderId;
        for (int i = 0; i < messages.size(); i++){
            Message message = messages.get(i);
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("text", message.getText());
            messageMap.put("senderId", message.getSender().getUserId());
            messageMap.put("senderName", message.getSender().getUsername());
            messageMap.put("departureTime", message.getTimeByFormat("HH:mm"));
            messageMap.put("userAvatarUrl", message.getSender().getAvatarImageUrl());

            List<String> subMessages = new ArrayList<>();

            lastSenderId = message.getSender().getUserId();

            for (int j = i + 1; j < messages.size(); j++, i++){
                Message subMessage = messages.get(j);
                if (Objects.equals(subMessage.getSender().getUserId(), lastSenderId)) {
                    subMessages.add(subMessage.getText());
                }else {
                    break;
                }
            }
            messageMap.put("subMessages", subMessages);
            messageList.add(messageMap);
        }
        return messageList;
    }
}
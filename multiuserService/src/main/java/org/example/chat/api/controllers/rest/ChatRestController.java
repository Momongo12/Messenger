package org.example.chat.api.controllers.rest;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.chat.api.model.Chat;
import org.example.chat.api.services.ChatService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ChatRestController {

    private final ChatService chatService;

    public static final String FETCH_CHATS = "/api/chats";
    public static final String CREATE_CHAT = "/api/chats/create";

    @GetMapping(value = FETCH_CHATS)
    public List<Chat> fetchChats(){
        return null;
    }

    @PostMapping(value = CREATE_CHAT)
    public String createChat(@RequestBody String chatName) {
        return chatService.createChat(chatName);
    }
}
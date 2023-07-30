package org.example.chat.api.services;

import org.example.chat.api.model.Chat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

    String createChat(String chatName);
    List<Chat> getChats();
}

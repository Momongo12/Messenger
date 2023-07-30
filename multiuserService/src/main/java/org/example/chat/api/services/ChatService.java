package org.example.chat.api.services;

import org.example.chat.api.model.Chat;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public interface ChatService {

    String createChat(String chatName);
    Stream<Chat> getChats();
}

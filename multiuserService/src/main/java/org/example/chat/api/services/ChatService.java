package org.example.chat.api.services;

import org.example.chat.api.model.Chat;
import org.example.chat.api.model.Message;
import org.example.chat.api.model.dtos.MessageDto;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public interface ChatService {

    String createChat(String chatName);

    void saveMessage(MessageDto messageDto, String chatId);

    Stream<Message> getMessages(String chatId);

    Stream<Chat> getChats();
}

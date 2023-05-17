package com.example.messenger.service;

import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;

import java.util.List;

public interface ChatService {
    Chat findByChatId(Long chatId);


    boolean saveMessage(Message message);

    void saveChat(Chat chat);

    List<Message> getChatMessages(Long chatId);

    void updateLastMessageByChatId(Long chatId, String lastMessage);
}

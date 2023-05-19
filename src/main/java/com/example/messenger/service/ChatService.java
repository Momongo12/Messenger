package com.example.messenger.service;

import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;
import com.example.messenger.model.User;

import java.util.List;
import java.util.Map;

public interface ChatService {
    Chat findByChatId(Long chatId);


    boolean saveMessage(Message message);

    void createChat(Chat chat, User firstUser, String secondUserUniqueUsername) throws IllegalArgumentException;

    List<Message> getChatMessages(Long chatId);

    void updateLastMessageByChatId(Long chatId, String lastMessage);

    List<Map<String, Object>> convertChatsListToChatsMapList(List<Chat> chats, User currentUser);

    List<Map<String, Object>> getChatMessagesMapsList(Long chatId);
}

package com.example.messenger.service;

import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.service.ChatService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    public Chat findByChatId(Long chatId) {
        Optional<Chat> chatOptional = chatRepository.findById(chatId);
        Chat chat;
        if (chatOptional.isPresent()) {
            chat = chatOptional.get();
            Hibernate.initialize(chat.getMembers());
            return chat;
        }
        return null;
    }

    public boolean saveMessage(Message message){
        try {
            messageRepository.save(message);
            return true;
        }catch (Exception e){
            // add record to logger
            return false;
        }
    }

    public void saveChat(Chat chat) {
        chatRepository.save(chat);
    }

    public List<Message> getChatMessages(Long chatId) {
        return messageRepository.findMessagesByChatIdOrderByDateAsc(chatId);
    }

    public void updateLastMessageByChatId(Long chatId, String lastMessage) {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isPresent()) {
            Chat chat = optionalChat.get();
            chat.setLastMessage(lastMessage);
            chatRepository.save(chat);
        }
    }
}

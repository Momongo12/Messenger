package org.example.chat.api.services.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.chat.api.controllers.ws.ChatWsController;
import org.example.chat.api.mappers.ChatMapper;
import org.example.chat.api.model.Chat;
import org.example.chat.api.model.Participant;
import org.example.chat.api.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Log4j2
@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final SetOperations<String, Chat> setOperations;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private  ChatMapper chatMapper;
    private static final String KEY = "messenger:chats";

    @Override
    public String createChat(String chatName) {

        log.info("Chat \"%s\" created.".formatted(chatName));

        Chat chat = Chat.builder()
                .name(chatName)
                .build();

        setOperations.add(KEY, chat);

        messagingTemplate.convertAndSend(
                ChatWsController.FETCH_CREATE_CHAT_EVENT,
                chatMapper.chatToChatDto(chat)
        );

        return chat.getId();
    }

    @Override
    public List<Chat> getChats() {
        return null;
    }
}

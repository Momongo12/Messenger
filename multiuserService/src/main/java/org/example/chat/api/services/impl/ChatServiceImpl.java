package org.example.chat.api.services.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.chat.api.controllers.ws.ChatWsController;
import org.example.chat.api.mappers.ChatMapper;
import org.example.chat.api.mappers.MessageMapper;
import org.example.chat.api.model.Chat;
import org.example.chat.api.model.Message;
import org.example.chat.api.model.dtos.MessageDto;
import org.example.chat.api.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;


@Log4j2
@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final SetOperations<String, Chat> setOperations;
    private final SetOperations<String, Message> setOperationsForMessages;
    private final MessageMapper messageMapper;
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
    public void saveMessage(MessageDto messageDto, String chatId) {

        Message message = messageMapper.messageDtoToMessage(messageDto);

        setOperationsForMessages.add(ChatKeyHelper.makeKey(chatId), message);

        log.debug("Message saved to %s chat".formatted(chatId));
    }

    @Override
    public Stream<Chat> getChats() {
        return Optional
                .ofNullable(setOperations.members(KEY))
                .orElseGet(HashSet::new)
                .stream();
    }

    private static class ChatKeyHelper {

        private static final String KEY = "messenger:chats:{chatId}:messages";

        public static String makeKey(String chatId) {

            return KEY.replace("{chatId}", chatId);
        }
    }
}

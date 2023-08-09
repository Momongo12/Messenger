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
import org.springframework.data.redis.core.RedisTemplate;
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

    private final RedisTemplate<String, String> redisTemplate;
    private final SetOperations<String, Chat> chats;
    private final SetOperations<String, Message> setOperationsForMessages;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMapper chatMapper;

    private static final String MESSENGER_CHATS_KEY = "messenger:chats";

    @Override
    public String createChat(String chatName) {

        Chat chat = Chat.builder()
                .name(chatName)
                .build();

        chats.add(MESSENGER_CHATS_KEY, chat);

        log.info("Chat \"%s\" created.".formatted(chatName));

        return chat.getId();
    }

    @Override
    public void deleteChat(String chatId) {

        //remove chat messages
        redisTemplate.delete(ChatKeyHelper.makeKeyForChatMessagesSet(chatId));

        //deleting chat member data
        redisTemplate.delete(ChatKeyHelper.makeKeyForChatParticipantsSet(chatId));

        //delete chat
        getChats()
                .filter(chat -> chat.getId().equals(chatId))
                .findAny()
                .ifPresent(chat -> {

                    chats.remove(MESSENGER_CHATS_KEY, chat);

                    log.info("Chat \"%s\" deleted.".formatted(chat.getName()));
                });
    }

    @Override
    public void saveMessage(MessageDto messageDto, String chatId) {

        Message message = messageMapper.messageDtoToMessage(messageDto);

        setOperationsForMessages.add(ChatKeyHelper.makeKeyForChatMessagesSet(chatId), message);

        log.info("Message saved to %s chat".formatted(chatId));
    }

    @Override
    public Stream<Message> getMessages(String chatId) {

        log.info("Fetching messages for chatId: {}", chatId);

        return Optional.
                ofNullable(setOperationsForMessages.members(ChatKeyHelper.makeKeyForChatMessagesSet(chatId)))
                .orElseGet(HashSet::new)
                .stream();
    }

    @Override
    public Stream<Chat> getChats() {
        return Optional
                .ofNullable(chats.members(MESSENGER_CHATS_KEY))
                .orElseGet(HashSet::new)
                .stream();
    }

    private static class ChatKeyHelper {

        private static final String KEY_TO_MULTIPLE_CHAT_MESSAGES = "messenger:chats:{chatId}:messages";

        private static final String KEY_TO_MULTIPLE_CHAT_PARTICIPANTS = "messenger:chats{chatId}:participants";

        public static String makeKeyForChatMessagesSet(String chatId) {

            return KEY_TO_MULTIPLE_CHAT_MESSAGES.replace("{chatId}", chatId);
        }

        public static String makeKeyForChatParticipantsSet(String chatId) {

            return KEY_TO_MULTIPLE_CHAT_PARTICIPANTS.replace("{chatId}", chatId);
        }

    }
}

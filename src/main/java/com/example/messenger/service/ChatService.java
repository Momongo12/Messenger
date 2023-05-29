package com.example.messenger.service;

import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;
import com.example.messenger.model.User;

import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @author Denis Moskvin
 * The ChatService interface provides methods for managing chat-related operations.
 */
public interface ChatService {

    /**
     * Finds a chat by its chatId.
     *
     * @param chatId The ID of the chat to find.
     * @return The Chat object with the specified chatId, or null if not found.
     */
    Chat findByChatId(Long chatId);

    /**
     * Saves a message in the chat.
     *
     * @param message The Message object to save.
     * @return true if the message is successfully saved, false otherwise.
     */
    boolean saveMessage(Message message);

    /**
     * Creates a chat between two users.
     *
     * @param chat                 The Chat object to create.
     * @param firstUser            The first user participating in the chat.
     * @param secondUserUniqueUsername The unique username of the second user participating in the chat.
     * @throws IllegalArgumentException if the chat creation fails due to invalid arguments.
     */
    void createChat(Chat chat, User firstUser, String secondUserUniqueUsername) throws IllegalArgumentException;

    /**
     * Retrieves the messages of a chat.
     *
     * @param chatId The ID of the chat.
     * @return The list of messages in the chat.
     */
    List<Message> getChatMessages(Long chatId);

    /**
     * Updates the last message of a chat.
     *
     * @param chatId      The ID of the chat.
     * @param lastMessage The new last message.
     */
    void updateLastMessageByChatId(Long chatId, String lastMessage);

    /**
     * Converts a list of Chat objects to a list of maps representing the chats.
     *
     * @param chats      The list of Chat objects.
     * @param currentUser The current user accessing the chats.
     * @return The list of maps representing the chats.
     */
    List<Map<String, Object>> convertChatsListToChatsMapList(List<Chat> chats, User currentUser);

    /**
     * Retrieves the messages of a chat as a list of maps.
     *
     * @param chatId The ID of the chat.
     * @return The list of messages in the chat as maps.
     */
    List<Map<String, Object>> getChatMessagesMapsList(Long chatId);
}
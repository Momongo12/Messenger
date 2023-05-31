package com.example.messenger.service;

import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;
import com.example.messenger.model.User;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.MessageRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @version : 1.0
 * @author : Denis Moskvin
 * The ChatServiceImpl class implements the {@link ChatService} interface and provides
 * methods for managing chat-related operations.
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

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

    @Transactional
    public void createChat(Chat chat, User firstUser, String secondUserUniqueUsername) throws IllegalArgumentException {
        User secondUser = userService.getUserByUniqueUsername(secondUserUniqueUsername);

        if (secondUser == null) throw new IllegalArgumentException();

        List<User> usersList = new ArrayList<>();
        usersList.add(firstUser);
        usersList.add(secondUser);

        chat.setMembers(usersList);
        chatRepository.save(chat);

        if (firstUser.getChats() != null) {
            firstUser.getChats().add(chat);
        }else {
            List<Chat> chats = new ArrayList<>();
            chats.add(chat);
            firstUser.setChats(chats);
        }
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

    public List<Map<String, Object>> convertChatsListToChatsMapList(List<Chat> chats, User currentUser) {
        List<Map<String, Object>> chatsList = new ArrayList<>();

        for (Chat chat : chats) {
            Map<String, Object> chatMap = new HashMap<>();
            chatMap.put("chatId", chat.getChatId());
            chatMap.put("chatAvatarImageUrl", imageService.getAvatarImageUrlByUser(chat.getInterlocutorForUser(currentUser)));
            chatMap.put("chatName", chat.getChatName(currentUser));
            chatMap.put("lastMessage", chat.getLastMessage());
            chatMap.put("interlocutorStatus", userService.isUserOnline(chat.getChatName(currentUser)) ? "online" : "offline");

            chatsList.add(chatMap);
        }
        return chatsList;
    }
    public List<Map<String, Object>> getChatMessagesMapsList(Long chatId){
        List<Message> messages = getChatMessages(chatId);
        List<Map<String, Object>> messageList = new ArrayList<>();
        Long lastSenderId;
        for (int i = 0; i < messages.size(); i++){
            Message message = messages.get(i);
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("text", message.getText());
            messageMap.put("senderId", message.getSender().getUserId());
            messageMap.put("senderName", message.getSender().getUsername());
            messageMap.put("departureTime", message.getMessageTimeBySpecifyFormat("HH:mm"));
            messageMap.put("userAvatarUrl", imageService.getAvatarImageUrlByUser(message.getSender()));

            List<String> subMessages = new ArrayList<>();

            lastSenderId = message.getSender().getUserId();

            for (int j = i + 1; j < messages.size(); j++, i++){
                Message subMessage = messages.get(j);
                if (Objects.equals(subMessage.getSender().getUserId(), lastSenderId)) {
                    subMessages.add(subMessage.getText());
                }else {
                    break;
                }
            }
            messageMap.put("subMessages", subMessages);
            messageList.add(messageMap);
        }
        return messageList;
    }

}

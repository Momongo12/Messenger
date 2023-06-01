package com.example.messenger.controller;


import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;
import com.example.messenger.model.User;
import com.example.messenger.service.ChatService;
import com.example.messenger.service.ImageService;
import com.example.messenger.service.SmileysService;
import com.example.messenger.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * This is the ChatController class that handles requests related to chats.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@Controller
@Log4j2
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SmileysService smileysService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    /**
     * Retrieves the list of chats for the authenticated user.
     *
     * @param model          the model to be populated with data
     * @param authentication the authentication object containing the current user's information
     * @return the view name for rendering the chat page
     */
    @GetMapping("/chats")
    public String getChats(Model model, Authentication authentication) {
        User user = userService.findUserByUserId(((User) authentication.getPrincipal()).getUserId());

        model.addAttribute("chats", getChatsMapsListByUser(user));
        model.addAttribute("currentUser", authentication.getPrincipal());
        model.addAttribute("smileysCategoriesList", smileysService.getSmileysCategoriesList());
        model.addAttribute("displayInputField", false);
        model.addAttribute("chatId", null);

        return "chat";
    }

    /**
     * Retrieves a specific chat based on the user ID of the other participant.
     *
     * @param model          the model to be populated with data
     * @param userId         the user ID of the other participant in the chat
     * @param authentication the authentication object containing the current user's information
     * @return the view name for rendering the chat page
     */
    @GetMapping("/chats/user={userId}")
    public String getChatByUserId(Model model, @PathVariable Long userId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<Chat> chats = currentUser.getChats();
        Chat chat = null;

        for (Chat cht : chats) {
            if (Objects.equals(cht.getInterlocutorId(currentUser), userId)) {
                chat = cht;
            }
        }

        assert chat != null;

        Hibernate.initialize(chat);

        model.addAttribute("chats", getChatsMapsListByUser(currentUser));
        model.addAttribute("currentUser", authentication.getPrincipal());
        model.addAttribute("chatName", chat.getChatName(currentUser));
        model.addAttribute("chatId", chat.getChatId());
        model.addAttribute("messages", chatService.getChatMessagesMapsList(chat.getChatId()));
        model.addAttribute("smileysCategoriesList", smileysService.getSmileysCategoriesList());
        model.addAttribute("displayInputField", true);

        return "chat";
    }

    private List<Map<String, Object>> getChatsMapsListByUser(User user) {
        List<Chat> chats = user.getChats();

        List<Map<String, Object>> chatsMapsList = new ArrayList<>();

        for (Chat chat: chats) {
            Map<String, Object> chatMap = new HashMap<>();

            chatMap.put("chatId", chat.getChatId());
            chatMap.put("chatName", chat.getChatName(user));
            chatMap.put("lastMessage", chat.getLastMessage());
            chatMap.put("chatAvatarImageUrl", imageService.getAvatarImageUrlByUser(chat.getInterlocutorForUser(user)));

            chatsMapsList.add(chatMap);
        }

        return chatsMapsList;
    }

    /**
     * Creates a new chat between the current user and the user with the specified username.
     *
     * @param chat                       the chat object containing the details of the new chat
     * @param secondInterlocutorUniqueUsername the unique username of the second user
     * @param authentication             the authentication object containing the current user's information
     * @return a response entity with the current chat ID and the list of chats for the current user
     */
    @PostMapping("api/chats/{secondInterlocutorUniqueUsername}")
    public ResponseEntity<Map<String, Object>> createChat(@RequestBody Chat chat, @PathVariable String secondInterlocutorUniqueUsername,
                                                          Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();

        try {
            chatService.createChat(chat, currentUser, secondInterlocutorUniqueUsername);

            List<Map<String, Object>> chatsList = chatService.convertChatsListToChatsMapList(currentUser.getChats(), currentUser);

            response.put("currentChatId", chat.getChatId());
            response.put("chatsList", chatsList);

            return ResponseEntity.ok().body(response);
        } catch (Exception ex) {
            log.error("Error occurred while creating a chat: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a specific chat based on the chat ID.
     *
     * @param model          the model to be populated with data
     * @param chatId         the ID of the chat to be retrieved
     * @param authentication the authentication object containing the current user's information
     * @return the view name for rendering the chat page
     */
    @GetMapping("/chats/chat={chatId}")
    public String getChatByChatId(Model model, @PathVariable Long chatId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Chat chat = chatService.findByChatId(chatId);

        model.addAttribute("chats", getChatsMapsListByUser(currentUser));
        model.addAttribute("currentUser", authentication.getPrincipal());
        model.addAttribute("chatId", chatId);
        model.addAttribute("messages", chatService.getChatMessagesMapsList(chat.getChatId()));
        model.addAttribute("smileysCategoriesList", smileysService.getSmileysCategoriesList());
        model.addAttribute("displayInputField", true);

        return "chat";
    }

    /**
     * Retrieves the messages for a specific chat.
     *
     * @param chatId the ID of the chat
     * @return a map containing the messages for the chat
     */
    @GetMapping("/chats/{chatId}/messages")
    @ResponseBody
    public Map<String, Object> getChatMessages(@PathVariable Long chatId) {
        Map<String, Object> response = new HashMap<>();

        response.put("messages", chatService.getChatMessagesMapsList(chatId));
        return response;
    }

    /**
     * Creates a new message in a chat.
     *
     * @param message       the message object containing the details of the new message
     * @param authentication the authentication object containing the current user's information
     * @return a map containing the updated list of messages and the status of the message creation
     */
    @PostMapping("/chats/messages")
    @ResponseBody
    public Map<String, Object> createMessage(@RequestBody Message message, Authentication authentication) {
        chatService.updateLastMessageByChatId(message.getChatId(), message.getText());

        Map<String, Object> response = new HashMap<>();
        message.setSender((User) authentication.getPrincipal());
        message.setMessageId(null);
        boolean statusSaveMessage = chatService.saveMessage(message);

        List<Map<String, Object>> messageList = chatService.getChatMessagesMapsList(message.getChatId());

        response.put("messages", messageList);
        response.put("status", statusSaveMessage ? 200 : 500);

        return response;
    }
}

package org.example.chat.api.controllers.ws;


import lombok.RequiredArgsConstructor;
import org.example.chat.api.model.dtos.MessageDto;
import org.example.chat.api.services.ChatService;
import org.example.chat.api.services.ParticipantService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;


@RequiredArgsConstructor
@Controller
public class ChatWsController {

    private final ChatService chatService;

    private final ParticipantService participantService;

    private final SimpMessagingTemplate messagingTemplate;

    public static final String FETCH_CREATE_CHAT_EVENT = "/topic/chats.create.event";
    public static final String FETCH_DELETE_CHAT_EVENT = "/topic/chats.delete.event";

    public static final String HANDLE_SEND_MESSAGE_TO_ALL = "/chats.{chatId}.messages.send";
    public static final String SEND_MESSAGE_TO_ALL = "/topic/chats.{chatId}.messages.send";
    public static final String SEND_MESSAGE_TO_PARTICIPANT = "/topic/chats.{chatId}.participants.{participantId}.messages.send";

    public static final String FETCH_MESSAGES = "/topic/chats.{chatId}.messages";
    public static final String FETCH_PERSONAL_MESSAGES = "/topic/chats.{chatId}.participants.{participantId}";

    @CrossOrigin
    @MessageMapping(HANDLE_SEND_MESSAGE_TO_ALL)
    @SendTo(SEND_MESSAGE_TO_ALL)
    private MessageDto handleSendMessageToAll(@DestinationVariable String chatId, @Payload MessageDto messageDto){

        chatService.saveMessage(messageDto, chatId);

        return messageDto;
    }


}

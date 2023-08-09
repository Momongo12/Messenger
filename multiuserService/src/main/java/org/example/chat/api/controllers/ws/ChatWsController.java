package org.example.chat.api.controllers.ws;


import lombok.RequiredArgsConstructor;
import org.example.chat.api.mappers.MessageMapper;
import org.example.chat.api.model.dtos.MessageDto;
import org.example.chat.api.services.ChatService;
import org.example.chat.api.services.ParticipantService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;


@RequiredArgsConstructor
@Controller
public class ChatWsController {

    private final ChatService chatService;

    private final ParticipantService participantService;

    private final SimpMessagingTemplate messagingTemplate;

    private final MessageMapper messageMapper;

    public static final String HANDLE_SEND_MESSAGE_TO_ALL = "/chats.{chatId}.messages.send";
    public static final String SEND_MESSAGE_TO_ALL = "/topic/chats.{chatId}.messages.send";
    public static final String SEND_MESSAGE_TO_PARTICIPANT = "/topic/chats.{chatId}.participants.{participantId}.messages.send";

    public static final String FETCH_MESSAGES = "/chats.{chatId}.messages";
    public static final String FETCH_PERSONAL_MESSAGES = "/topic/chats.{chatId}.participants.{participantId}";

    @CrossOrigin
    @MessageMapping(HANDLE_SEND_MESSAGE_TO_ALL)
    @SendTo(SEND_MESSAGE_TO_ALL)
    private MessageDto handleSendMessageToAll(@DestinationVariable String chatId, @Payload MessageDto messageDto){

        chatService.saveMessage(messageDto, chatId);

        return messageDto;
    }

    @CrossOrigin
    @SubscribeMapping(FETCH_MESSAGES)
    private List<MessageDto> fetchMessages(@DestinationVariable String chatId) {

        return chatService.getMessages(chatId)
                .map(messageMapper::messageToMessageDto)
                .toList();
    }


}

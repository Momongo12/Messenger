package org.example.chat.api.controllers.ws;


import lombok.RequiredArgsConstructor;
import org.example.chat.api.services.ChatService;
import org.example.chat.api.services.ParticipantService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@RequiredArgsConstructor
@Controller
public class ChatWsController {

    private final ChatService chatService;

    private final ParticipantService participantService;

    private final SimpMessagingTemplate messagingTemplate;

    public static final String FETCH_CREATE_CHAT_EVENT = "/topic/chats.create.event";
    public static final String FETCH_DELETE_CHAT_EVENT = "/topic/chats.delete.event";

    public static final String SEND_MESSAGE_TO_ALL = "/topic/chats.{chat_id}.messages.send";
    public static final String SEND_MESSAGE_TO_PARTICIPANT = "/topic/chats.{chat_id}.participants.{participant_id}.messages.send";

    public static final String FETCH_MESSAGES = "/topic/chats.{chat_id}.messages";
    public static final String FETCH_PERSONAL_MESSAGES = "/topic/chats.{chat_id}.participants.{participant_id}";
}

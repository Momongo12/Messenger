package org.example.chat.api.controllers.ws;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.chat.api.model.dtos.ParticipantDto;
import org.example.chat.api.services.ParticipantService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;


@Log4j2
@RequiredArgsConstructor
@Controller
public class ParticipantWsController{

    public static final String FETCH_PARTICIPANT_JOIN_IN_CHAT = "/chats.{chatId}.participants.join";
    public static final String FETCH_PARTICIPANT_LEAVE_FROM_CHAT = "/topic/chats.{chatId}.participants.leave";
    private final ParticipantService participantService;
    private final SimpMessagingTemplate messagingTemplate;

//    @CrossOrigin
//    @SubscribeMapping("/topic" + FETCH_PARTICIPANT_JOIN_IN_CHAT)
//    public String handleJoinChat(@DestinationVariable String chatId) {
//        log.info("handle join to %s chat".formatted(chatId));
//
//        messagingTemplate.convertAndSend("/topic" + FETCH_PARTICIPANT_JOIN_IN_CHAT.replace("{chatId}", chatId),
//                );
//        return "You joined to chat with %s id".formatted(chatId);
//    }

    @CrossOrigin
    @MessageMapping(FETCH_PARTICIPANT_JOIN_IN_CHAT)
    @SendTo("/topic" + FETCH_PARTICIPANT_JOIN_IN_CHAT)
    public ParticipantDto notifyParticipantJoin(@DestinationVariable String chatId, @Payload ParticipantDto participantDto) {

        participantService.hundleJoinChat(participantDto, chatId);

        return participantDto;
    }

}

package org.example.chat.api.controllers.rest;


import lombok.RequiredArgsConstructor;
import org.example.chat.api.mappers.ParticipantMapper;
import org.example.chat.api.model.dtos.ParticipantDto;
import org.example.chat.api.services.ParticipantService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ParticipantRestController {

    private final ParticipantService participantService;
    private final ParticipantMapper participantMapper;

    public static final String FETCH_PARTICIPANTS = "/api/chats/{chatId}/participants";

    @GetMapping(FETCH_PARTICIPANTS)
    public List<ParticipantDto> fetchParticipants(@PathVariable String chatId) {

        return participantService
                .getParticipants(chatId)
                .map(participantMapper::participantToParticipantDto)
                .toList();
    }


}

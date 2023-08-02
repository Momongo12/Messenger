package org.example.chat.api.services;

import org.example.chat.api.model.dtos.ParticipantDto;

public interface ParticipantService {

    void hundleJoinChat(ParticipantDto participantDto, String chatId);
}

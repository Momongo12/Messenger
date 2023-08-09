package org.example.chat.api.services;

import org.example.chat.api.model.Participant;
import org.example.chat.api.model.dtos.ParticipantDto;

import java.util.stream.Stream;

public interface ParticipantService {

    Stream<Participant> getParticipants(String chatId);

    void hundleJoinChat(ParticipantDto participantDto, String chatId);
}

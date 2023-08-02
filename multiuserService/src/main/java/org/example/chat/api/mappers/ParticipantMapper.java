package org.example.chat.api.mappers;

import org.example.chat.api.model.Participant;
import org.example.chat.api.model.dtos.ParticipantDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {

    Participant participantDtoToParticipant(ParticipantDto participantDto);
    ParticipantDto participantToParticipantDto(Participant participant);
}

package org.example.chat.api.mappers;

import org.example.chat.api.model.Message;
import org.example.chat.api.model.dtos.MessageDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageDto messageToMessageDto(Message message);

    Message messageDtoToMessage(MessageDto messageDto);
}

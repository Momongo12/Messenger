package org.example.chat.api.mappers;


import org.example.chat.api.model.Chat;
import org.example.chat.api.model.dtos.ChatDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    ChatDto chatToChatDto(Chat chat);
    Chat chatDtoToChat(ChatDto chatDto);
}

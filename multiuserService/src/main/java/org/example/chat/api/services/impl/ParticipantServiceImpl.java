package org.example.chat.api.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.chat.api.mappers.ParticipantMapper;
import org.example.chat.api.model.Participant;
import org.example.chat.api.model.dtos.ParticipantDto;
import org.example.chat.api.services.ParticipantService;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;


@Log4j2
@RequiredArgsConstructor
@Service
public class ParticipantServiceImpl implements ParticipantService {

    private final SetOperations<String, Participant> setOperations;
    private final ParticipantMapper participantMapper;

    @Override
    public void hundleJoinChat(ParticipantDto participantDto, String chatId) {

        Participant participant = participantMapper.participantDtoToParticipant(participantDto);

        if (participant.getChatId() == null) {
            participant.setChatId(chatId);
        }

        setOperations.add(ParticipantKeyHelper.makeKey(chatId), participant);

        log.info("Participant %s joined to %s chat".formatted(participant.getParticipantId(), chatId));
    }

    private static class ParticipantKeyHelper {

        private static final String KEY = "messenger:chats:{chat_id}:participants";

        public static String makeKey(String chatId) {
            return KEY.replace("{chat_id}", chatId);
        }
    }
}

package org.example.chat.api.services.impl;

import org.example.chat.api.services.ParticipantService;
import org.springframework.stereotype.Service;


@Service
public class ParticipantServiceImpl implements ParticipantService {

    private static class ParticipantKeyHelper {

        private static final String KEY = "messenger:chats:{chat_id}:participants";

        public static String makeKey(String chatId) {
            return KEY.replace("{chat_id}", chatId);
        }
    }
}

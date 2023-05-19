package com.example.messenger.service;

import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;
import com.example.messenger.model.User;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.service.ChatServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    void findByChatId_existingChatId_returnsChat() {
        Long chatId = 1L;
        Chat chat = new Chat();
        chat.setMembers(Arrays.asList(new User(), new User()));
        Mockito.when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        Chat result = chatService.findByChatId(chatId);

        Assertions.assertEquals(chat, result);
        Assertions.assertEquals(Arrays.asList(new User(), new User()), result.getMembers());
    }

    @Test
    void findByChatId_nonExistingChatId_returnsNull() {
        Long chatId = 1L;
        Mockito.when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        Chat result = chatService.findByChatId(chatId);

        Assertions.assertNull(result);
    }

    @Test
    void saveMessage_successfulSave_returnsTrue() throws Exception {
        Message message = new Message();

        boolean result = chatService.saveMessage(message);

        Assertions.assertTrue(result);
        Mockito.verify(messageRepository).save(message);
    }

    @Test
    void saveMessage_exceptionDuringSave_returnsFalse() {
        Message message = new Message();
        Mockito.doThrow(new RuntimeException("Some error")).when(messageRepository).save(message);

        boolean result = chatService.saveMessage(message);

        Assertions.assertFalse(result);
        Mockito.verify(messageRepository).save(message);
    }

    @Test
    void saveChat() {
        Chat chat = new Chat();

        chatService.saveChat(chat);

        Mockito.verify(chatRepository).save(chat);
    }

    @Test
    void getChatMessages() {
        Long chatId = 1L;
        List<Message> messages = Arrays.asList(new Message(), new Message());
        Mockito.when(messageRepository.findMessagesByChatIdOrderByDateAsc(chatId)).thenReturn(messages);

        List<Message> result = chatService.getChatMessages(chatId);

        Assertions.assertEquals(messages, result);
    }

    @Test
    void updateLastMessageByChatId_existingChatId_updatesLastMessage() {
        Long chatId = 1L;
        String lastMessage = "Hello, World!";
        Chat chat = new Chat();
        Mockito.when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        chatService.updateLastMessageByChatId(chatId, lastMessage);

        Assertions.assertEquals(lastMessage, chat.getLastMessage());
        Mockito.verify(chatRepository).save(chat);
    }

    @Test
    void updateLastMessageByChatId_nonExistingChatId_doesNotUpdateLastMessage() {
        Long chatId = 1L;
        String lastMessage = "Hello, World!";
        Mockito.when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        chatService.updateLastMessageByChatId(chatId, lastMessage);

        Mockito.verify(chatRepository, Mockito.never()).save(Mockito.any(Chat.class));
    }
}

package com.example.messenger.service;

import com.example.messenger.model.Chat;
import com.example.messenger.model.Message;
import com.example.messenger.model.User;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.MessageRepository;
import static org.junit.jupiter.api.Assertions.*;

import com.example.messenger.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Captor
    private ArgumentCaptor<Chat> chatCaptor;

    @Test
    void findByChatId_existingChatId_returnsChat() {
        Long chatId = 1L;
        Chat chat = new Chat();
        chat.setMembers(Arrays.asList(new User(), new User()));
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        Chat result = chatService.findByChatId(chatId);

        assertEquals(chat, result);
        assertEquals(Arrays.asList(new User(), new User()), result.getMembers());
    }

    @Test
    void findByChatId_nonExistingChatId_returnsNull() {
        Long chatId = 1L;
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        Chat result = chatService.findByChatId(chatId);

        assertNull(result);
    }

    @Test
    void saveMessage_successfulSave_returnsTrue(){
        Message message = new Message();

        boolean result = chatService.saveMessage(message);

        assertTrue(result);
        verify(messageRepository).save(message);
    }

    @Test
    void saveMessage_exceptionDuringSave_returnsFalse() {
        Message message = new Message();
        doThrow(new RuntimeException("Some error")).when(messageRepository).save(message);

        boolean result = chatService.saveMessage(message);

        assertFalse(result);
        verify(messageRepository).save(message);
    }

    @Test
    void getChatMessages() {
        Long chatId = 1L;
        List<Message> messages = Arrays.asList(new Message(), new Message());
        when(messageRepository.findMessagesByChatIdOrderByDateAsc(chatId)).thenReturn(messages);

        List<Message> result = chatService.getChatMessages(chatId);

        assertEquals(messages, result);
    }

    @Test
    void updateLastMessageByChatId_existingChatId_updatesLastMessage() {
        Long chatId = 1L;
        String lastMessage = "Hello, World!";
        Chat chat = new Chat();
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        chatService.updateLastMessageByChatId(chatId, lastMessage);

        assertEquals(lastMessage, chat.getLastMessage());
        verify(chatRepository).save(chat);
    }

    @Test
    void updateLastMessageByChatId_nonExistingChatId_doesNotUpdateLastMessage() {
        Long chatId = 1L;
        String lastMessage = "Hello, World!";
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        chatService.updateLastMessageByChatId(chatId, lastMessage);

        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void createChat_validParameters_savesChatWithCorrectMembers() {
        // Arrange
        User firstUser = new User();
        firstUser.setUserId(1L);

        User secondUser = new User();
        secondUser.setUserId(2L);
        String secondUserUniqueUsername = "secondUser";

        Chat chat = new Chat();
        List<User> usersList = new ArrayList<>();
        usersList.add(firstUser);
        usersList.add(secondUser);

        when(userService.getUserByUniqueUsername(secondUserUniqueUsername)).thenReturn(secondUser);

        // Act
        chatService.createChat(chat, firstUser, secondUserUniqueUsername);

        // Assert
        verify(chatRepository).save(chatCaptor.capture());
        Chat savedChat = chatCaptor.getValue();

        assertEquals(usersList, savedChat.getMembers());
    }

    @Test
    void createChat_invalidSecondUser_throwsIllegalArgumentException() {
        User firstUser = new User();
        firstUser.setUserId(1L);
        String secondUserUniqueUsername = "nonexistentUser";
        when(userService.getUserByUniqueUsername(secondUserUniqueUsername)).thenReturn(null);
        Chat chat = new Chat();

        assertThrows(IllegalArgumentException.class, () -> chatService.createChat(chat, firstUser, secondUserUniqueUsername));
    }
}

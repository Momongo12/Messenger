package com.example.messenger.repository;

import com.example.messenger.model.Chat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE chats SET last_message = :lastMessage WHERE chat_id = :chatId", nativeQuery = true)
    void updateLastMessage(@Param("chatId") Long chatId, @Param("lastMessage") String lastMessage);

//    @Query(value = "SELECT * FROM ")
}

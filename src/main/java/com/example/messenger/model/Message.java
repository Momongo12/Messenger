package com.example.messenger.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long messageId;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "text")
    private String text;

    @Column(name = "sent_at")
    private Date date;

    public Message(){

    }

}

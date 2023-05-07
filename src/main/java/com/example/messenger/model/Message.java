package com.example.messenger.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long messageId;

    @Column(name = "chat_id")
    private Long chatId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "text")
    private String text;

    @Column(name = "sent_at")
    private Timestamp date;

    public Message(){

    }
    public String getTimeByFormat(String format){
        LocalDateTime localDateTime = date.toLocalDateTime();
        DateTimeFormatter formatter;
        try {
            formatter = DateTimeFormatter.ofPattern(format);
        }catch (IllegalArgumentException e){
            System.err.println("Error format of dateTime" + e.getMessage());
            formatter = DateTimeFormatter.ofPattern("HH:mm");
        }

        return localDateTime.format(formatter);
    }
}

package com.example.messenger.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The Message entity represents a message in the application.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@Data
@Entity
@Table(name = "messages")
@Log4j2
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

    /**
     * Default constructor for the Message class.
     */
    public Message() {
    }

    /**
     * Retrieves the formatted message time based on the specified format.
     *
     * @param format the format of the message time
     * @return the formatted message time
     */
    public String getMessageTimeBySpecifyFormat(String format) {
        LocalDateTime localDateTime = date.toLocalDateTime();
        DateTimeFormatter formatter;
        try {
            formatter = DateTimeFormatter.ofPattern(format);
        } catch (IllegalArgumentException e) {
            log.error("Error format of dateTime: {}", e.getMessage());
            formatter = DateTimeFormatter.ofPattern("HH:mm");
        }

        return localDateTime.format(formatter);
    }
}
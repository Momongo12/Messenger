package com.example.messenger.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

/**
 * The Chat entity represents a chat in the application.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@Entity
@Table(name = "chats")
@Data
public class Chat {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long chatId;

    @Column(name = "name")
    private String chatName;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "members_number")
    private int membersNumber;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatId")
    private List<Message> messages;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "chat_users", joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> members;

    /**
     * Default constructor for the Chat class.
     */
    public Chat() {
    }

    /**
     * Retrieves the chat name based on the current user.
     *
     * @param currentUser the current user
     * @return the chat name
     */
    public String getChatName(User currentUser) {
        if (membersNumber != 2) {
            return chatName;
        }
        for (User member : members) {
            if (!member.equals(currentUser)) {
                return member.getUsername();
            }
        }
        return chatName;
    }

    /**
     * Retrieves the interlocutor's user ID based on the current user.
     *
     * @param currentUser the current user
     * @return the interlocutor's user ID
     */
    public Long getInterlocutorId(User currentUser) {
        for (User member : members) {
            if (!member.equals(currentUser)) {
                return member.getUserId();
            }
        }
        return null;
    }

    /**
     * Retrieves the interlocutor's user based on the current user.
     *
     * @param currentUser the current user
     * @return the interlocutor's user
     */
    public User getInterlocutorForUser(User currentUser) {
        for (User member : members) {
            if (!member.equals(currentUser)) {
                return member;
            }
        }
        return null;
    }

    /**
     * Retrieves the chat avatar image URL for the current user.
     *
     * @param currentUser the current user
     * @return the chat avatar image URL
     */
    public String getChatAvatarImageUrlForUser(User currentUser) {
        for (User member : members) {
            if (!member.equals(currentUser)) {
                return member.getAvatarImageUrl();
            }
        }
        return null;
    }
}
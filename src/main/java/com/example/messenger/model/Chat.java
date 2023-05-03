package com.example.messenger.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

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
    public Chat(){

    }

    public String getChatName(User currentUser){
        if (membersNumber != 2) {
            return chatName;
        }
        for (User member: members){
            if (!member.equals(currentUser)){
                return member.getUsername();
            }
        }
        return chatName;
    }

}

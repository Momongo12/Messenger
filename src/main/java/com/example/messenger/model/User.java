package com.example.messenger.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.File;
import java.util.*;


/**
 * The User entity represents a user in the application.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@Entity
@Table(name = "users")
@Data
@Log4j2
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "unique_username")
    private String uniqueUsername;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "chat_users", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id"))
    private List<Chat> chats;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserImages userImages;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MyUserDetails userDetails;

    /**
     * Default constructor for the User class.
     */
    public User() {
    }

    /**
     * Retrieves the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    /**
     * Retrieves the list of chats for the given username prefix.
     *
     * @param usernamePrefix the prefix of the username
     * @return the list of chats
     */
    public List<Chat> getChatsForUsernamePrefix(String usernamePrefix) {
        List<Chat> chatsForUsernamePrefix = new LinkedList<>();
        for (Chat chat : chats) {
            if (chat.getMembersNumber() == 2 && chat.getInterlocutorForUser(this).getUniqueUsername().startsWith(usernamePrefix)) {
                chatsForUsernamePrefix.add(chat);
            }
        }
        return chatsForUsernamePrefix;
    }

    /**
     * Retrieves the avatar image URL for the user.
     *
     * @return the avatar image URL
     */
    public String getAvatarImageUrl() {
        if (userImages != null && userImages.getAvatarImageUrl() != null) {
            return userImages.getAvatarImageUrl();
        } else if (userImages == null) {
            userImages = new UserImages(this);
        }
        if (userImages.getDefaultAvatarImageUrl() == null) {
            File directory = new File("src/main/resources/static/images/defaultImages");
            int filesNumber = directory.listFiles((dir, name) -> name.startsWith("defaultAvatar") && name.endsWith(".jpg")).length;
            int randomFileNumber = new Random().nextInt(filesNumber) + 1;
            userImages.setDefaultAvatarImageUrl("/images/defaultImages/" + "defaultAvatar" + randomFileNumber + ".jpg");
        }

        return userImages.getDefaultAvatarImageUrl();
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public boolean equals(Object user) {
        if (user instanceof User) {
            return Objects.equals(this.getUserId(), ((User) user).getUserId());
        } else {
            return false;
        }
    }
}
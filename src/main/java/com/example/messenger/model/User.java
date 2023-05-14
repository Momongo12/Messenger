package com.example.messenger.model;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.File;
import java.util.*;


@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "unique_username")
    private String uniqueUsername;

    @Column(name = "short_info")
    private String shortInfo;

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

    public User(){
    }

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

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public String getUniqueUsername() {
        return uniqueUsername;
    }

    public void setUniqueUsername(String uniqueUsername) {
        this.uniqueUsername = uniqueUsername;
    }

    public UserImages getUserImages() {
        return userImages;
    }

    public void setUserImages(UserImages userImages) {
        this.userImages = userImages;
    }

    public List<Chat> getChatsForUsernamePrefix(String usernamePrefix){
        List<Chat> chatsForUsernamePrefix = new LinkedList<>();
        for (Chat chat: chats){
            if (chat.getMembersNumber() == 2 && chat.getChatName(this).startsWith(usernamePrefix)){
                chatsForUsernamePrefix.add(chat);
            }
        }
        return chatsForUsernamePrefix;
    }

    public String getAvatarImageUrl(){
        if (userImages != null && userImages.getAvatarImageUrl() != null){
            return userImages.getAvatarImageUrl();
        }else if (userImages == null){
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

    public String getProfileBgImageUrl() {
        if (userImages != null && userImages.getProfileBgImageUrl() != null) {
            return userImages.getProfileBgImageUrl();
        } else if (userImages == null) {
            userImages = new UserImages(this);
        }

        if (userImages.getDefaultProfileBgImageUrl() == null) {
            File directory = new File("src/main/resources/static/images/defaultImages");
            int filesNumber = directory.listFiles((dir, name) -> name.startsWith("defaultProfileBg") && name.endsWith(".jpg")).length;
            int randomFileNumber = new Random().nextInt(filesNumber) + 1;
            userImages.setDefaultProfileBgImageUrl("/images/defaultImages/" + "defaultProfileBg" + randomFileNumber + ".jpg");
        }

        return userImages.getDefaultProfileBgImageUrl();
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

}
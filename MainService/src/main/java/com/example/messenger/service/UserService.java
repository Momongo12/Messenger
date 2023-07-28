package com.example.messenger.service;


import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Chat;
import com.example.messenger.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

/**
 * The UserService interface provides operations related to user management.
 *
 * @version 1.0
 * @author Denis Moskvin
 * @see UserDetailsService
 * @see User
 */
public interface UserService extends UserDetailsService {

    /**
     * Retrieves a user by email.
     *
     * @param email the email address of the user
     * @return the user with the specified email or null if not found
     */
    User findByEmail(String email);

    /**
     * Retrieves a user by user ID.
     *
     * @param userId the ID of the user
     * @return the user with the specified user ID or null if not found
     */
    User findUserByUserId(Long userId);

    /**
     * Creates a new user.
     *
     * @param user the user to be created
     * @return true if the user was created successfully, false otherwise
     */
    boolean createUser(User user);

    /**
     * Retrieves a list of users as maps with specific details by unique username prefix, excluding users with existing chats.
     *
     * @param usernamePrefix the prefix of the unique username
     * @param chats          the list of existing chats
     * @param currentUser    the current user
     * @return the list of users as maps with the specified details
     */
    List<Map<String, Object>> getUsersMapsListByUniqueUsernamePrefixWithoutExistChats(String usernamePrefix,
                                                                                      List<Chat> chats, User currentUser);

    /**
     * Updates user details based on the provided UserInfoDto.
     *
     * @param userInfoDto the UserInfoDto object containing updated user details
     * @param user        the user to be updated
     * @throws IllegalArgumentException if the provided user details are invalid
     */
    void updateUserDetails(UserInfoDto userInfoDto, User user) throws IllegalArgumentException;

    /**
     * Retrieves a user by unique username.
     *
     * @param uniqueUsername the unique username of the user
     * @return the user with the specified unique username or null if not found
     */
    User getUserByUniqueUsername(String uniqueUsername);

    /**
     * Checks if a user is online based on the username.
     *
     * @param username the username of the user
     * @return true if the user is online, false otherwise
     */
    boolean isUserOnline(String username);
}
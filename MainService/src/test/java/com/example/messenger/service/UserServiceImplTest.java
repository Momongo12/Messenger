package com.example.messenger.service;

import com.example.messenger.dto.UserInfoDto;
import com.example.messenger.model.Chat;
import com.example.messenger.model.MyUserDetails;
import com.example.messenger.model.User;
import com.example.messenger.model.UserImages;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRegistry sessionRegistry;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testFindUserByUserId_ExistingUser() {
        // Prepare
        Long userId = 1L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.findUserByUserId(userId);

        // Assert
        assertSame(expectedUser, result);
    }

    @Test
    public void testFindUserByUserId_NonExistingUser() {
        // Prepare
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        User result = userService.findUserByUserId(userId);

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetUsersMapsListByUniqueUsernamePrefixWithoutExistChats() {
        // Prepare
        String usernamePrefix = "test";
        List<Chat> chats = new ArrayList<>();
        User currentUser = new User();
        currentUser.setUserId(1L);

        User user1 = new User();
        user1.setUserId(2L);
        user1.setUsername("test1");
        user1.setUniqueUsername("unique1");
        user1.setUserImages(new UserImages());
        user1.getUserImages().setAvatarImageName("avatar1.jpg");
        user1.setUserDetails(new MyUserDetails());
        user1.getUserDetails().setPublicProfileFlag(true);

        User user2 = new User();
        user2.setUserId(3L);
        user2.setUsername("test2");
        user2.setUniqueUsername("unique2");
        user2.setUserImages(new UserImages());
        user2.getUserImages().setAvatarImageName("avatar2.jpg");
        user2.setUserDetails(new MyUserDetails());
        user2.getUserDetails().setPublicProfileFlag(true);

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findByUniqueUsernameStartingWith(usernamePrefix)).thenReturn(users);
        when(imageService.getAvatarImageUrlByUser(user1)).thenReturn(user1.getUserImages().getAvatarImageName());
        when(imageService.getAvatarImageUrlByUser(user2)).thenReturn(user2.getUserImages().getAvatarImageName());

        // Act
        List<Map<String, Object>> result = userService.getUsersMapsListByUniqueUsernamePrefixWithoutExistChats(usernamePrefix, chats, currentUser);

        // Assert
        assertEquals(2, result.size());

        Map<String, Object> userMap1 = result.get(0);
        assertEquals(0, userMap1.get("chatId"));
        assertEquals("test1", userMap1.get("username"));
        assertEquals("unique1", userMap1.get("uniqueUsername"));
        assertEquals("avatar1.jpg", userMap1.get("chatAvatarImageUrl"));

        Map<String, Object> userMap2 = result.get(1);
        assertEquals(0, userMap2.get("chatId"));
        assertEquals("test2", userMap2.get("username"));
        assertEquals("unique2", userMap2.get("uniqueUsername"));
        assertEquals("avatar2.jpg", userMap2.get("chatAvatarImageUrl"));
    }

    @Test
    public void testUpdateUserDetails_Success() {
        // Prepare
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUniqueUsername("newUniqueUsername");
        userInfoDto.setUsername("newUsername");
        userInfoDto.setUserDescription("newDescription");
        userInfoDto.setEmail("newEmail@example.com");
        userInfoDto.setPhoneNumber("123456789");
        userInfoDto.setIsPublicProfile(true);
        userInfoDto.setIsShowingEmail(true);

        User user = new User();
        user.setUniqueUsername("oldUniqueUsername");
        user.setUsername("oldUsername");
        user.setUserDetails(new MyUserDetails());
        user.getUserDetails().setShortInfo("oldDescription");
        user.setEmail("oldEmail@example.com");
        user.getUserDetails().setPhoneNumber("987654321");
        user.getUserDetails().setPublicProfileFlag(false);
        user.getUserDetails().setShowingEmailFlag(false);

        when(userRepository.findByUniqueUsername(userInfoDto.getUniqueUsername())).thenReturn(null);

        // Act
        userService.updateUserDetails(userInfoDto, user);

        // Assert
        assertEquals("newUniqueUsername", user.getUniqueUsername());
        assertEquals("newUsername", user.getUsername());
        assertEquals("newDescription", user.getUserDetails().getShortInfo());
        assertEquals("newEmail@example.com", user.getEmail());
        assertEquals("123456789", user.getUserDetails().getPhoneNumber());
        assertTrue(user.getUserDetails().isPublicProfileFlag());
        assertTrue(user.getUserDetails().isShowingEmailFlag());
        verify(userRepository, times(1)).save(user);
    }

    @Test()
    public void testUpdateUserDetails_DuplicateUniqueUsername() {
        // Prepare
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUniqueUsername("existingUniqueUsername");
        userInfoDto.setIsShowingEmail(true);
        userInfoDto.setIsPublicProfile(true);

        User user = new User();
        user.setUserDetails(new MyUserDetails());
        user.setUniqueUsername("existingUniqueUsername");

        User existingUser = new User();
        when(userRepository.findByUniqueUsername(userInfoDto.getUniqueUsername())).thenReturn(existingUser);

        // Act
        userService.updateUserDetails(userInfoDto, user);
    }

    @Test
    public void testGetUserByUniqueUsername_ExistingUser() {
        // Prepare
        String uniqueUsername = "testUser";
        User expectedUser = new User();
        when(userRepository.findByUniqueUsername(uniqueUsername)).thenReturn(expectedUser);

        // Act
        User result = userService.getUserByUniqueUsername(uniqueUsername);

        // Assert
        assertSame(expectedUser, result);
    }

    @Test
    public void testGetUserByUniqueUsername_NonExistingUser() {
        // Prepare
        String uniqueUsername = "nonExistingUser";
        when(userRepository.findByUniqueUsername(uniqueUsername)).thenReturn(null);

        // Act
        User result = userService.getUserByUniqueUsername(uniqueUsername);

        // Assert
        assertNull(result);
    }

    @Test
    public void testIsUserOnline_UserOnline() {
        // Prepare
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        List<Object> principals = Collections.singletonList(user);
        SessionInformation sessionInformation = mock(SessionInformation.class);
        when(sessionRegistry.getAllPrincipals()).thenReturn(principals);
        when(sessionRegistry.getAllSessions(user, false)).thenReturn(Collections.singletonList(sessionInformation));

        // Act
        boolean result = userService.isUserOnline(username);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsUserOnline_UserOffline() {
        // Prepare
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        List<Object> principals = Collections.singletonList(user);
        when(sessionRegistry.getAllPrincipals()).thenReturn(principals);
        when(sessionRegistry.getAllSessions(user, false)).thenReturn(Collections.emptyList());

        // Act
        boolean result = userService.isUserOnline(username);

        // Assert
        assertFalse(result);
    }

}

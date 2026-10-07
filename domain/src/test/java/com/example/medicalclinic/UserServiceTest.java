package com.example.medicalclinic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserJpaRepositoryPort userJpaRepositoryPort;
    private UserService userService;

    @BeforeEach
    void setup() {
        this.userJpaRepositoryPort = Mockito.mock(UserJpaRepositoryPort.class);
        this.userService = new UserService(userJpaRepositoryPort);
    }

    @Test
    void getAllUsers_DataCorrect_ReturnUsers() {
        User user = User.builder().id(1L).email("email").password("pass").build();
        User user2 = User.builder().id(2L).email("email2").password("pass2").build();
        Pageable pageable = Pageable.builder().pageNumber(0).pageSize(10).build();
        Page<User> userPage = Page.<User>builder()
                .content(List.of(user, user2))
                .pageNumber(0).pageSize(10).totalElements(2).totalPages(1)
                .build();
        when(userJpaRepositoryPort.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userService.getAllUsers(pageable);

        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals("email", result.getContent().get(0).getEmail()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals("email2", result.getContent().get(1).getEmail())
        );
    }

    @Test
    void getUserByEmail_DataCorrect_ReturnUser() {
        User user = User.builder().id(1L).email("email").password("pass").build();
        when(userJpaRepositoryPort.findByEmail("email")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("email");

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("email", result.getEmail())
        );
    }

    @Test
    void addUser_DataCorrect_ReturnUser() {
        AddUserCommand command = AddUserCommand.builder().email("email").password("pass").build();
        User savedUser = User.builder().id(1L).email("email").password("pass").build();
        when(userJpaRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        User result = userService.addUser(command);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("email", result.getEmail())
        );
    }

    @Test
    void getUserByEmail_UserNotFound_ThrowsUserNotFoundException() {
        when(userJpaRepositoryPort.findByEmail("email")).thenReturn(Optional.empty());

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class, () -> userService.getUserByEmail("email"));

        assertAll(
                () -> assertEquals("User with email email not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void updateUser_DataCorrect_UpdatesUser() {
        User existingUser = User.builder().id(1L).email("email").password("oldPass").build();
        UpdateUserRequest request = UpdateUserRequest.builder().email("updatedEmail").password("updatedPass").build();
        when(userJpaRepositoryPort.findByEmail("email")).thenReturn(Optional.of(existingUser));

        userService.updateUser("email", request);

        assertAll(
                () -> assertEquals("updatedEmail", existingUser.getEmail()),
                () -> assertEquals("updatedPass", existingUser.getPassword())
        );
        verify(userJpaRepositoryPort).findByEmail("email");
        verify(userJpaRepositoryPort).save(existingUser);
        verifyNoMoreInteractions(userJpaRepositoryPort);
    }

    @Test
    void updateUser_UserNotFound_ThrowsUserNotFoundException() {
        UpdateUserRequest request = UpdateUserRequest.builder().email("updatedEmail").password("updatedPass").build();
        when(userJpaRepositoryPort.findByEmail("email")).thenReturn(Optional.empty());

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class, () -> userService.updateUser("email", request));

        assertAll(
                () -> assertEquals("User with email email not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void deleteUser_DataCorrect_DeletesUser() {
        userService.deleteUser("email");

        verify(userJpaRepositoryPort).deleteByEmail("email");
        verifyNoMoreInteractions(userJpaRepositoryPort);
    }
}

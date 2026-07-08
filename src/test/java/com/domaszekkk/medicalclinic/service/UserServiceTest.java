package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddUserCommand;
import com.domaszekkk.medicalclinic.dto.UserDto;
import com.domaszekkk.medicalclinic.entity.User;
import com.domaszekkk.medicalclinic.mapper.UserMapper;
import com.domaszekkk.medicalclinic.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserJpaRepository userJpaRepository;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setup() {
        this.userJpaRepository = Mockito.mock(UserJpaRepository.class);
        this.userMapper = Mappers.getMapper(UserMapper.class);
        this.userService = new UserService(userJpaRepository, userMapper);
    }

    @Test
    void getAllUsers_DataCorrect_ReturnUsers() {
        // given
        User user = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("email2")
                .password("pass2")
                .build();

        List<User> users = List.of(user, user2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());
        when(userJpaRepository.findAll(pageable)).thenReturn(userPage);

        // when
        Page<UserDto> result = userService.getAllUsers(pageable);

        // then
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
        // given
        User user = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();

        when(userJpaRepository.findByEmail("email")).thenReturn(Optional.of(user));

        // when
        UserDto result = userService.getUserByEmail("email");

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("email", result.getEmail())
        );
    }

    @Test
    void addUser_DataCorrect_ReturnUser() {
        // given
        AddUserCommand command = new AddUserCommand();
        command.setEmail("email");
        command.setPassword("pass");

        User savedUser = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();

        when(userJpaRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserDto result = userService.addUser(command);

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("email", result.getEmail())
        );
    }
}
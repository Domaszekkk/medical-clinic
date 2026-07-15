package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.AddUserCommand;
import com.domaszekkk.medicalclinic.dto.UpdateUserRequest;
import com.domaszekkk.medicalclinic.dto.UserDto;
import com.domaszekkk.medicalclinic.exception.UserNotFoundException;
import com.domaszekkk.medicalclinic.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;

    @Test
    void getAllUsers_DataCorrect_ReturnsUsers() throws Exception {
        // given
        UserDto user = UserDto.builder()
                .id(1L)
                .email("email")
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .email("email2")
                .build();

        Page<UserDto> page = new PageImpl<>(List.of(user, user2), PageRequest.of(0, 10), 2);
        when(userService.getAllUsers(any())).thenReturn(page);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("email"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].email").value("email2"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getUserByEmail_DataCorrect_ReturnsUser() throws Exception {
        // given
        UserDto user = UserDto.builder()
                .id(1L)
                .email("email")
                .build();

        when(userService.getUserByEmail("email")).thenReturn(user);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{email}", "email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"));
    }

    @Test
    void getUserByEmail_UserNotFound_Returns404() throws Exception {
        // given
        when(userService.getUserByEmail("email")).thenThrow(new UserNotFoundException("email"));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{email}", "email"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with email email not found"));
    }

    @Test
    void addUser_DataCorrect_ReturnsCreatedUser() throws Exception {
        // given
        AddUserCommand command = AddUserCommand.builder()
                .email("email")
                .password("password")
                .build();

        UserDto savedUser = UserDto.builder()
                .id(1L)
                .email("email")
                .build();

        when(userService.addUser(any(AddUserCommand.class))).thenReturn(savedUser);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"));
    }

    @Test
    void updateUser_DataCorrect_Returns204() throws Exception {
        // given
        UpdateUserRequest request = new UpdateUserRequest("updatedEmail", "updatedPassword");

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{email}", "email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(userService).updateUser(eq("email"), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_UserNotFound_Returns404() throws Exception {
        // given
        UpdateUserRequest request = new UpdateUserRequest("updatedEmail", "updatedPassword");
        doThrow(new UserNotFoundException("email")).when(userService).updateUser(eq("email"), any(UpdateUserRequest.class));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{email}", "email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with email email not found"));
    }

    @Test
    void deleteUser_DataCorrect_Returns204() throws Exception {
        // when + then
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{email}", "email"))
                .andExpect(status().isNoContent());
        verify(userService).deleteUser("email");
    }
}
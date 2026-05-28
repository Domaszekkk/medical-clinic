package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.AddUserRequest;
import com.domaszekkk.medicalclinic.dto.UpdateUserRequest;
import com.domaszekkk.medicalclinic.dto.UserDto;
import com.domaszekkk.medicalclinic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{email}")
    public UserDto getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody AddUserRequest request) {
        return userService.addUser(request);
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable String email, @RequestBody UpdateUserRequest request) {
        userService.updateUser(email, request);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
    }
}
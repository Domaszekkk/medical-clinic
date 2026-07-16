package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.AddUserCommand;
import com.domaszekkk.medicalclinic.dto.PageResponse;
import com.domaszekkk.medicalclinic.dto.UpdateUserRequest;
import com.domaszekkk.medicalclinic.dto.UserDto;
import com.domaszekkk.medicalclinic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user accounts")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get all users", description = "Returns a paginated list of all users")
    @GetMapping
    public PageResponse<UserDto> getAllUsers(Pageable pageable) {
        return PageResponse.of(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Get user by email", description = "Returns a single user identified by their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{email}")
    public UserDto getUserByEmail(@Parameter(description = "Email of the user to retrieve") @PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @Operation(summary = "Create a new user", description = "Creates a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody AddUserCommand request) {
        return userService.addUser(request);
    }

    @Operation(summary = "Update user", description = "Updates the email and password of an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@Parameter(description = "Email of the user to update") @PathVariable String email, @RequestBody UpdateUserRequest request) {
        userService.updateUser(email, request);
    }

    @Operation(summary = "Delete user", description = "Deletes a user account by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully")
    })
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Parameter(description = "Email of the user to delete") @PathVariable String email) {
        userService.deleteUser(email);
    }
}
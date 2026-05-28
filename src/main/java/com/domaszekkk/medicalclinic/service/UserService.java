package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddUserRequest;
import com.domaszekkk.medicalclinic.dto.UpdateUserRequest;
import com.domaszekkk.medicalclinic.dto.UserDto;
import com.domaszekkk.medicalclinic.entity.User;
import com.domaszekkk.medicalclinic.exception.PatientNotFoundException;
import com.domaszekkk.medicalclinic.exception.UserNotFoundException;
import com.domaszekkk.medicalclinic.mapper.UserMapper;
import com.domaszekkk.medicalclinic.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers() {
        return userMapper.mapToDtoList(userJpaRepository.findAll());
    }

    public UserDto getUserByEmail(String email) {
        User user = userJpaRepository
                .findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        return userMapper.mapToDto(user);
    }

    public UserDto addUser(AddUserRequest request) {
        User user = userMapper.mapToEntity(request);
        return userMapper.mapToDto(userJpaRepository.save(user));
    }

    public void updateUser(String email, UpdateUserRequest request) {
        User user = userJpaRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        userJpaRepository.save(user);
    }

    public void deleteUser(String email) {
        userJpaRepository.deleteByEmail(email);
    }
}
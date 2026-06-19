package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddUserCommand;
import com.domaszekkk.medicalclinic.dto.UpdateUserRequest;
import com.domaszekkk.medicalclinic.dto.UserDto;
import com.domaszekkk.medicalclinic.entity.User;
import com.domaszekkk.medicalclinic.exception.UserNotFoundException;
import com.domaszekkk.medicalclinic.mapper.UserMapper;
import com.domaszekkk.medicalclinic.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userJpaRepository.findAll(pageable)
                .map(userMapper::mapToDto);
    }

    public UserDto getUserByEmail(String email) {
        User user = userJpaRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return userMapper.mapToDto(user);
    }

    public UserDto addUser(AddUserCommand request) {
        User user = userMapper.mapToEntity(request);
        return userMapper.mapToDto(userJpaRepository.save(user));
    }

    public void updateUser(String email, UpdateUserRequest request) {
        User user = userJpaRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.update(userMapper.mapToEntity(request));
        userJpaRepository.save(user);
    }
    @Transactional
    public void deleteUser(String email) {
        userJpaRepository.deleteByEmail(email);
    }
}
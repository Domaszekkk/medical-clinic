package com.example.medicalclinic.user;

import com.example.medicalclinic.Page;
import com.example.medicalclinic.PageFactory;
import com.example.medicalclinic.Pageable;
import com.example.medicalclinic.User;
import com.example.medicalclinic.UserJpaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserJpaRepositoryAdapter implements UserJpaRepositoryPort {
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public Page<User> findAll(Pageable pageable) {
        return PageFactory.from(
                userJpaRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())),
                userMapper::toModel);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(userMapper::toModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userMapper::toModel);
    }

    @Override
    public User save(User user) {
        return userMapper.toModel(userJpaRepository.save(userMapper.toEntity(user)));
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        userJpaRepository.deleteByEmail(email);
    }
}

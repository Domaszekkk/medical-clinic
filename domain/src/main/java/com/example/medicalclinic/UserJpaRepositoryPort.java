package com.example.medicalclinic;


import java.util.Optional;

public interface UserJpaRepositoryPort {
    Optional<User> findById(Long userId);

    Page<User> findAll(Pageable pageable);

    Optional<User> findByEmail(String email);

    User save(User user);

    void deleteByEmail(String email);
}

package com.example.medicalclinic;

import java.util.Optional;

public interface UserJpaRepositoryPort {
    Page<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    User save(User user);

    void deleteByEmail(String email);
}

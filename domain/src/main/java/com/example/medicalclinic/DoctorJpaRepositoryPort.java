package com.example.medicalclinic;

import java.util.Optional;

public interface DoctorJpaRepositoryPort {
    Page<Doctor> findAll(String specialization, Pageable pageable);

    Optional<Doctor> findById(Long id);

    boolean existsById(Long id);

    Doctor save(Doctor doctor);

    void deleteById(Long id);
}

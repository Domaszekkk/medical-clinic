package com.example.medicalclinic;

import java.util.Optional;

public interface PatientJpaRepositoryPort {
    Page<Patient> findAll(Pageable pageable);

    Optional<Patient> findById(Long id);

    boolean existsById(Long id);

    Optional<Patient> findByUserEmail(String email);

    Patient save(Patient patient);

    void deleteByUserEmail(String email);
}

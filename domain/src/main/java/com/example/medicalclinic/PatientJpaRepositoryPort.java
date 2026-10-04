package com.example.medicalclinic;

import java.util.List;
import java.util.Optional;

public interface PatientJpaRepositoryPort {
    Page<Patient> findAll(Pageable pageable);
    Patient save(Patient patient);

    void deleteByUserEmail(String email);

    List<Patient> findByUserEmail(String email);

    Optional<Patient> findById(Long id);
}

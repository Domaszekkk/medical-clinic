package com.example.medicalclinic.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, Long> {
    Optional<PatientEntity> findByUserEmail(String email);

    void deleteByUserEmail(String email);
}

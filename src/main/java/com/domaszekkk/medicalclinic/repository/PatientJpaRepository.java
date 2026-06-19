package com.domaszekkk.medicalclinic.repository;

import com.domaszekkk.medicalclinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientJpaRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserEmail(String email);
    void deleteByUserEmail(String email);
}
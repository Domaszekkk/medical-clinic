package com.domaszekkk.medicalclinic.repository;

import com.domaszekkk.medicalclinic.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorJpaRepository extends JpaRepository<Doctor, Long> {
    Page<Doctor> findBySpecialization(String specialization, Pageable pageable);
}

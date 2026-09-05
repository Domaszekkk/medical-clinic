package com.domaszekkk.medicalclinic.repository;

import com.domaszekkk.medicalclinic.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DoctorJpaRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {
    Page<Doctor> findBySpecialization(String specialization, Pageable pageable);
}

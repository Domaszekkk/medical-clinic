package com.domaszekkk.medicalclinic.repository;

import com.domaszekkk.medicalclinic.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorJpaRepository extends JpaRepository<Doctor, Long> {
}

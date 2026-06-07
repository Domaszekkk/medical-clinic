package com.domaszekkk.medicalclinic.repository;

import com.domaszekkk.medicalclinic.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitJpaRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPatientId(Long patientId);
    boolean existsByDoctorIdAndDateTime(Long doctorId, LocalDateTime dateTime);

}

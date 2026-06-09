package com.domaszekkk.medicalclinic.repository;

import com.domaszekkk.medicalclinic.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitJpaRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPatientId(Long patientId);

    @Query("SELECT visit FROM Visit visit " +
            "WHERE visit.doctor.id = :doctorId " +
            "AND visit.startDateTime < :endDateTime " +
            "AND visit.endDateTime > :startDateTime")
    List<Visit> findConflictingVisits(
            @Param("doctorId") Long doctorId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}

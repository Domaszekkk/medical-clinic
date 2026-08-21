package com.domaszekkk.medicalclinic.repository;

import com.domaszekkk.medicalclinic.entity.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitJpaRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit> {

    Page<Visit> findByPatientId(Long patientId, Pageable pageable);

    @Query("SELECT v FROM Visit v " +
            "WHERE v.doctor.id = :doctorId " +
            "AND v.startDateTime < :endDateTime " +
            "AND v.endDateTime > :startDateTime")
    List<Visit> findConflictingVisits(@Param("doctorId") Long doctorId,
                                      @Param("startDateTime") LocalDateTime startDateTime,
                                      @Param("endDateTime") LocalDateTime endDateTime);
}
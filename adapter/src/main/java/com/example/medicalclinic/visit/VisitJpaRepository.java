package com.example.medicalclinic.visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitJpaRepository extends JpaRepository<VisitEntity, Long>, JpaSpecificationExecutor<VisitEntity> {

    @Query("SELECT v FROM VisitEntity v " +
            "WHERE v.doctor.id = :doctorId " +
            "AND v.startDateTime < :endDateTime " +
            "AND v.endDateTime > :startDateTime")
    List<VisitEntity> findConflictingVisits(@Param("doctorId") Long doctorId,
                                            @Param("startDateTime") LocalDateTime startDateTime,
                                            @Param("endDateTime") LocalDateTime endDateTime);
}

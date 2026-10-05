package com.example.medicalclinic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitJpaRepositoryPort {
    Page<Visit> findAll(VisitFilter filter, Pageable pageable);

    Optional<Visit> findById(Long id);

    Visit save(Visit visit);

    void delete(Visit visit);

    List<Visit> findConflictingVisits(Long doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}

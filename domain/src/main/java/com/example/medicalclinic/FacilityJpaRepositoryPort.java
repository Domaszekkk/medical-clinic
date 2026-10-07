package com.example.medicalclinic;

import java.util.Optional;

public interface FacilityJpaRepositoryPort {
    Page<Facility> findAll(Pageable pageable);

    Optional<Facility> findById(Long id);

    boolean existsById(Long id);

    Facility save(Facility facility);

    void deleteById(Long id);
}

package com.domaszekkk.medicalclinic.repository;

import com.domaszekkk.medicalclinic.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityJpaRepository extends JpaRepository<Facility, Long> {
}

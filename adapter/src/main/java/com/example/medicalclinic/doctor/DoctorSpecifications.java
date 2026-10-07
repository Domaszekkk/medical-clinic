package com.example.medicalclinic.doctor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DoctorSpecifications {

    public static Specification<DoctorEntity> hasSpecialization(String specialization) {
        return (root, query, cb) -> specialization == null ? null
                : cb.equal(root.get("specialization"), specialization);
    }
}

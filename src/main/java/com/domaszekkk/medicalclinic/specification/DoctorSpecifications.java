package com.domaszekkk.medicalclinic.specification;

import com.domaszekkk.medicalclinic.entity.Doctor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DoctorSpecifications {

    public static Specification<Doctor> hasSpecialization(String specialization) {
        return (root, query, cb) -> specialization == null ? null
                : cb.equal(root.get("specialization"), specialization);
    }
}
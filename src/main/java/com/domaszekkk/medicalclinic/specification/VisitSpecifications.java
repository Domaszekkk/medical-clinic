package com.domaszekkk.medicalclinic.specification;

import com.domaszekkk.medicalclinic.entity.Visit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VisitSpecifications {

    public static Specification<Visit> isAvailable() {
        return (root, query, cb) -> cb.isNull(root.get("patient"));
    }

    public static Specification<Visit> hasDoctorId(Long doctorId) {
        return (root, query, cb) -> doctorId == null ? null : cb.equal(root.get("doctor").get("id"), doctorId);
    }

    public static Specification<Visit> hasPatientId(Long patientId) {
        return (root, query, cb) -> patientId == null ? null : cb.equal(root.get("patient").get("id"), patientId);
    }

    public static Specification<Visit> hasSpecialization(String specialization) {
        return (root, query, cb) -> specialization == null ? null
                : cb.equal(root.get("doctor").get("specialization"), specialization);
    }

    public static Specification<Visit> startsAtOrAfter(LocalDateTime from) {
        return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("startDateTime"), from);
    }

    public static Specification<Visit> startsBefore(LocalDateTime to) {
        return (root, query, cb) -> to == null ? null : cb.lessThan(root.get("startDateTime"), to);
    }

    public static Specification<Visit> startsAfter(LocalDateTime moment) {
        return (root, query, cb) -> moment == null ? null : cb.greaterThan(root.get("startDateTime"), moment);
    }

    public static Specification<Visit> endsBefore(LocalDateTime moment) {
        return (root, query, cb) -> moment == null ? null : cb.lessThan(root.get("endDateTime"), moment);
    }
}
package com.example.medicalclinic.visit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VisitSpecifications {

    private static final String START_DATE_TIME = "startDateTime";

    public static Specification<VisitEntity> isAvailable() {
        return (root, query, cb) -> cb.isNull(root.get("patient"));
    }

    public static Specification<VisitEntity> hasDoctorId(Long doctorId) {
        return (root, query, cb) -> doctorId == null ? null : cb.equal(root.get("doctor").get("id"), doctorId);
    }

    public static Specification<VisitEntity> hasPatientId(Long patientId) {
        return (root, query, cb) -> patientId == null ? null : cb.equal(root.get("patient").get("id"), patientId);
    }

    public static Specification<VisitEntity> hasSpecialization(String specialization) {
        return (root, query, cb) -> specialization == null ? null
                : cb.equal(root.get("doctor").get("specialization"), specialization);
    }

    public static Specification<VisitEntity> startsAtOrAfter(LocalDateTime from) {
        return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get(START_DATE_TIME), from);
    }

    public static Specification<VisitEntity> startsBefore(LocalDateTime to) {
        return (root, query, cb) -> to == null ? null : cb.lessThan(root.get(START_DATE_TIME), to);
    }

    public static Specification<VisitEntity> startsAfter(LocalDateTime moment) {
        return (root, query, cb) -> moment == null ? null : cb.greaterThan(root.get(START_DATE_TIME), moment);
    }

    public static Specification<VisitEntity> endsBefore(LocalDateTime moment) {
        return (root, query, cb) -> moment == null ? null : cb.lessThan(root.get("endDateTime"), moment);
    }
}

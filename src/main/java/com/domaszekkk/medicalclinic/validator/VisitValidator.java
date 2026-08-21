package com.domaszekkk.medicalclinic.validator;

import com.domaszekkk.medicalclinic.entity.Doctor;
import com.domaszekkk.medicalclinic.entity.Facility;
import com.domaszekkk.medicalclinic.entity.Visit;
import com.domaszekkk.medicalclinic.exception.DoctorNotAssignedToFacilityException;
import com.domaszekkk.medicalclinic.exception.DoctorVisitConflictException;
import com.domaszekkk.medicalclinic.exception.InvalidVisitDateException;
import com.domaszekkk.medicalclinic.exception.VisitAlreadyTakenException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VisitValidator {

    public static void validateVisitDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidVisitDateException("Cannot create visit in the past");
        }

        if (startDateTime.getMinute() % 15 != 0 || endDateTime.getMinute() % 15 != 0) {
            throw new InvalidVisitDateException("Visit must be at full quarter of an hour");
        }

        if (!endDateTime.isAfter(startDateTime)) {
            throw new InvalidVisitDateException("Visit end date must be after start date");
        }
    }

    public static void validateConflictingVisits(List<Visit> conflictingVisits, LocalDateTime startDateTime) {
        if (!conflictingVisits.isEmpty()) {
            throw new DoctorVisitConflictException(startDateTime);
        }
    }

    public static void validatePatientRegistration(Visit visit) {
        if (visit.getPatient() != null) {
            throw new VisitAlreadyTakenException(visit.getId());
        }

        if (visit.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidVisitDateException("Cannot register for past visit");
        }
    }

    public static void validateDoctorAssignedToFacility(Doctor doctor, Facility facility) {
        boolean isAssigned = doctor.getFacilities().stream()
                .anyMatch(assignedFacility -> assignedFacility.getId().equals(facility.getId()));
        if (!isAssigned) {
            throw new DoctorNotAssignedToFacilityException(doctor.getId(), facility.getId());
        }
    }

    public static void validateDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new InvalidVisitDateException("Both 'from' and 'to' must be provided");
        }
        if (!to.isAfter(from)) {
            throw new InvalidVisitDateException("'to' must be after 'from'");
        }
    }
}
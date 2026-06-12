package com.domaszekkk.medicalclinic.validator;

import com.domaszekkk.medicalclinic.entity.Visit;
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

        if (startDateTime.getMinute() % 15 != 0) {
            throw new InvalidVisitDateException("Visit must be at full quarter of an hour");
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
}
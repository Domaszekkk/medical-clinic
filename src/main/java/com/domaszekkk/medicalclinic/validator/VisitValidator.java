package com.domaszekkk.medicalclinic.validator;

import com.domaszekkk.medicalclinic.entity.Visit;
import com.domaszekkk.medicalclinic.exception.DoctorVisitConflictException;
import com.domaszekkk.medicalclinic.exception.InvalidVisitDateException;
import com.domaszekkk.medicalclinic.exception.VisitAlreadyTakenException;
import com.domaszekkk.medicalclinic.repository.VisitJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VisitValidator {
    private final VisitJpaRepository visitJpaRepository;

    public void validateVisitDate(Long doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidVisitDateException("Cannot create visit in the past");
        }

        if (startDateTime.getMinute() % 15 != 0) {
            throw new InvalidVisitDateException("Visit must be at full quarter of an hour");
        }

        List<Visit> conflictingVisits = visitJpaRepository.findConflictingVisits(doctorId, startDateTime, endDateTime);
        if (!conflictingVisits.isEmpty()) {
            throw new DoctorVisitConflictException(startDateTime);
        }
    }

    public void validatePatientRegistration(Visit visit) {
        if (visit.getPatient() != null) {
            throw new VisitAlreadyTakenException(visit.getId());
        }

        if (visit.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidVisitDateException("Cannot register for past visit");
        }
    }
}
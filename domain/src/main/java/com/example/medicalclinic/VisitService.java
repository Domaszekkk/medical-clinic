package com.example.medicalclinic;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class VisitService {
    private final VisitJpaRepositoryPort visitJpaRepositoryPort;
    private final DoctorJpaRepositoryPort doctorJpaRepositoryPort;
    private final FacilityJpaRepositoryPort facilityJpaRepositoryPort;
    private final PatientJpaRepositoryPort patientJpaRepositoryPort;

    public Visit addVisit(Long doctorId, AddVisitCommand command) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        Facility facility = findFacilityOrThrow(command.getFacilityId());

        VisitValidator.validateDoctorAssignedToFacility(doctor, facility);
        VisitValidator.validateVisitDate(command.getStartDateTime(), command.getEndDateTime());
        validateNoConflictingVisits(doctorId, command.getStartDateTime(), command.getEndDateTime());

        Visit visit = Visit.from(command);
        visit.setDoctor(doctor);
        visit.setFacility(facility);
        return visitJpaRepositoryPort.save(visit);
    }

    public Visit registerPatientForVisit(Long visitId, Long patientId) {
        Visit visit = visitJpaRepositoryPort
                .findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException(visitId));

        Patient patient = patientJpaRepositoryPort
                .findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        VisitValidator.validatePatientRegistration(visit);

        visit.setPatient(patient);
        return visitJpaRepositoryPort.save(visit);
    }

    public Page<Visit> getVisits(VisitFilter filter, Pageable pageable) {
        validateFilter(filter);
        return visitJpaRepositoryPort.findAll(filter, pageable);
    }

    public void cancelVisit(Long visitId, Long doctorId) {
        Visit visit = visitJpaRepositoryPort
                .findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException(visitId));
        if (doctorId != null
                && (visit.getDoctor() == null || !Objects.equals(visit.getDoctor().getId(), doctorId))) {
            throw new VisitNotFoundException(visitId);
        }
        visitJpaRepositoryPort.delete(visit);
    }

    private void validateFilter(VisitFilter filter) {
        if (filter.patientId() != null) {
            VisitValidator.validatePatientExists(patientJpaRepositoryPort.existsById(filter.patientId()), filter.patientId());
        }
        if (filter.doctorId() != null) {
            VisitValidator.validateDoctorExists(doctorJpaRepositoryPort.existsById(filter.doctorId()), filter.doctorId());
        }
        if (filter.from() != null && filter.to() != null) {
            VisitValidator.validateDateRange(filter.from(), filter.to());
        }
    }

    private Doctor findDoctorOrThrow(Long doctorId) {
        return doctorJpaRepositoryPort.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
    }

    private Facility findFacilityOrThrow(Long facilityId) {
        return facilityJpaRepositoryPort.findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException(facilityId));
    }

    private void validateNoConflictingVisits(Long doctorId, LocalDateTime start, LocalDateTime end) {
        List<Visit> conflictingVisits = visitJpaRepositoryPort.findConflictingVisits(doctorId, start, end);
        VisitValidator.validateConflictingVisits(conflictingVisits, start);
    }
}

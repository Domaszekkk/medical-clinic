package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.VisitDto;
import com.domaszekkk.medicalclinic.dto.VisitScope;
import com.domaszekkk.medicalclinic.entity.Doctor;
import com.domaszekkk.medicalclinic.entity.Facility;
import com.domaszekkk.medicalclinic.entity.Patient;
import com.domaszekkk.medicalclinic.entity.Visit;
import com.domaszekkk.medicalclinic.exception.DoctorNotFoundException;
import com.domaszekkk.medicalclinic.exception.FacilityNotFoundException;
import com.domaszekkk.medicalclinic.exception.PatientNotFoundException;
import com.domaszekkk.medicalclinic.exception.VisitNotFoundException;
import com.domaszekkk.medicalclinic.mapper.VisitMapper;
import com.domaszekkk.medicalclinic.repository.DoctorJpaRepository;
import com.domaszekkk.medicalclinic.repository.FacilityJpaRepository;
import com.domaszekkk.medicalclinic.repository.PatientJpaRepository;
import com.domaszekkk.medicalclinic.repository.VisitJpaRepository;
import com.domaszekkk.medicalclinic.specification.VisitSpecifications;
import com.domaszekkk.medicalclinic.validator.VisitValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitJpaRepository visitJpaRepository;
    private final DoctorJpaRepository doctorJpaRepository;
    private final FacilityJpaRepository facilityJpaRepository;
    private final PatientJpaRepository patientJpaRepository;
    private final VisitMapper visitMapper;

    public VisitDto addVisit(Long doctorId, AddVisitCommand command) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        Facility facility = findFacilityOrThrow(command.getFacilityId());

        VisitValidator.validateDoctorAssignedToFacility(doctor, facility);
        VisitValidator.validateVisitDate(command.getStartDateTime(), command.getEndDateTime());
        validateNoConflictingVisits(doctorId, command.getStartDateTime(), command.getEndDateTime());

        Visit visit = visitMapper.mapToEntity(command);
        visit.setDoctor(doctor);
        visit.setFacility(facility);
        return visitMapper.mapToDto(visitJpaRepository.save(visit));
    }

    public VisitDto registerPatientForVisit(Long visitId, Long patientId) {
        Visit visit = visitJpaRepository
                .findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException(visitId));

        Patient patient = patientJpaRepository
                .findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        VisitValidator.validatePatientRegistration(visit);

        visit.setPatient(patient);
        return visitMapper.mapToDto(visitJpaRepository.save(visit));
    }

    public Page<VisitDto> getVisits(Long patientId, Long doctorId, LocalDateTime from, LocalDateTime to,
                                    String specialization, Boolean available, VisitScope scope,
                                    Pageable pageable) {
        if (patientId != null && !patientJpaRepository.existsById(patientId)) {
            throw new PatientNotFoundException(patientId);
        }
        if (doctorId != null && !doctorJpaRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }
        if (from != null && to != null) {
            VisitValidator.validateDateRange(from, to);
        }

        Specification<Visit> spec = Specification.allOf(
                VisitSpecifications.hasPatientId(patientId),
                VisitSpecifications.hasDoctorId(doctorId),
                VisitSpecifications.startsAtOrAfter(from),
                VisitSpecifications.startsBefore(to),
                VisitSpecifications.hasSpecialization(specialization),
                Boolean.TRUE.equals(available) ? VisitSpecifications.isAvailable() : null,
                scopeSpecification(scope)
        );
        return visitJpaRepository.findAll(spec, pageable)
                .map(visitMapper::mapToDto);
    }

    private Specification<Visit> scopeSpecification(VisitScope scope) {
        if (scope == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        return switch (scope) {
            case PAST -> VisitSpecifications.endsBefore(now);
            case UPCOMING -> VisitSpecifications.startsAfter(now);
            case ALL -> null;
        };
    }

    @Transactional
    public void cancelVisit(Long visitId, Long doctorId) {
        Visit visit = visitJpaRepository.findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException(visitId));
        if (doctorId != null
                && (visit.getDoctor() == null || !Objects.equals(visit.getDoctor().getId(), doctorId))) {
            throw new VisitNotFoundException(visitId);
        }
        visitJpaRepository.delete(visit);
    }

    private Doctor findDoctorOrThrow(Long doctorId) {
        return doctorJpaRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
    }

    private Facility findFacilityOrThrow(Long facilityId) {
        return facilityJpaRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException(facilityId));
    }

    private void validateNoConflictingVisits(Long doctorId, LocalDateTime start, LocalDateTime end) {
        List<Visit> conflictingVisits = visitJpaRepository.findConflictingVisits(doctorId, start, end);
        VisitValidator.validateConflictingVisits(conflictingVisits, start);
    }
}
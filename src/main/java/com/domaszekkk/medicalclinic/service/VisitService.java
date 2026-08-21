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

    public Page<VisitDto> getPatientVisits(Long patientId, Pageable pageable) {
        if (!patientJpaRepository.existsById(patientId)) {
            throw new PatientNotFoundException(patientId);
        }
        return visitJpaRepository.findByPatientId(patientId, pageable)
                .map(visitMapper::mapToDto);
    }

    public Page<VisitDto> getAvailableVisits(Pageable pageable) {
        Specification<Visit> spec = VisitSpecifications.isAvailable();
        return visitJpaRepository.findAll(spec, pageable)
                .map(visitMapper::mapToDto);
    }

    public Page<VisitDto> getDoctorAvailableVisits(Long doctorId, Pageable pageable) {
        requireDoctorExists(doctorId);
        Specification<Visit> spec = Specification.allOf(VisitSpecifications.isAvailable(), VisitSpecifications.hasDoctorId(doctorId));
        return visitJpaRepository.findAll(spec, pageable)
                .map(visitMapper::mapToDto);
    }

    public Page<VisitDto> getDoctorVisits(Long doctorId, VisitScope scope, Pageable pageable) {
        requireDoctorExists(doctorId);
        LocalDateTime now = LocalDateTime.now();
        Specification<Visit> scopeSpec = switch (scope) {
            case PAST -> VisitSpecifications.endsBefore(now);
            case UPCOMING -> VisitSpecifications.startsAfter(now);
            case ALL -> null;
        };
        Specification<Visit> spec = Specification.allOf(VisitSpecifications.hasDoctorId(doctorId), scopeSpec);
        return visitJpaRepository.findAll(spec, pageable)
                .map(visitMapper::mapToDto);
    }

    @Transactional
    public void cancelVisit(Long doctorId, Long visitId) {
        Visit visit = visitJpaRepository.findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException(visitId));
        if (visit.getDoctor() == null || !visit.getDoctor().getId().equals(doctorId)) {
            throw new VisitNotFoundException(visitId);
        }
        visitJpaRepository.delete(visit);
    }

    public Page<VisitDto> getAvailableVisitsInRange(LocalDateTime from, LocalDateTime to, String specialization, Pageable pageable) {
        VisitValidator.validateDateRange(from, to);
        Specification<Visit> spec = Specification.allOf(VisitSpecifications.isAvailable(), dateRangeAndSpecializationSpec(from, to, specialization));
        return visitJpaRepository.findAll(spec, pageable)
                .map(visitMapper::mapToDto);
    }

    public Page<VisitDto> getVisitsInRange(LocalDateTime from, LocalDateTime to, String specialization, Pageable pageable) {
        VisitValidator.validateDateRange(from, to);
        return visitJpaRepository.findAll(dateRangeAndSpecializationSpec(from, to, specialization), pageable)
                .map(visitMapper::mapToDto);
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

    private void requireDoctorExists(Long doctorId) {
        if (!doctorJpaRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }
    }

    private Specification<Visit> dateRangeAndSpecializationSpec(LocalDateTime from, LocalDateTime to, String specialization) {
        return Specification.allOf(
                VisitSpecifications.startsAtOrAfter(from),
                VisitSpecifications.startsBefore(to),
                VisitSpecifications.hasSpecialization(specialization)
        );
    }
}
package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.VisitDto;
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
import com.domaszekkk.medicalclinic.validator.VisitValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        Doctor doctor = doctorJpaRepository
                .findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        Facility facility = facilityJpaRepository
                .findById(command.getFacilityId())
                        .orElseThrow(() -> new FacilityNotFoundException(command.getFacilityId()));

        VisitValidator.validateDoctorAssignedToFacility(doctor, facility);

        VisitValidator.validateVisitDate(command.getStartDateTime(), command.getEndDateTime());

        List<Visit> conflictingVisits = visitJpaRepository.findConflictingVisits(
                doctorId, command.getStartDateTime(), command.getEndDateTime());
        VisitValidator.validateConflictingVisits(conflictingVisits, command.getStartDateTime());

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
                .orElseThrow(() -> new PatientNotFoundException(patientId.toString()));

        VisitValidator.validatePatientRegistration(visit);

        visit.setPatient(patient);
        return visitMapper.mapToDto(visitJpaRepository.save(visit));
    }

    public Page<VisitDto> getPatientVisits(Long patientId, Pageable pageable) {
        return visitJpaRepository.findByPatientId(patientId, pageable)
                .map(visitMapper::mapToDto);
    }
}
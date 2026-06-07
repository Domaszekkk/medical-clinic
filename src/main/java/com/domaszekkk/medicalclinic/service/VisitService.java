package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.VisitDto;
import com.domaszekkk.medicalclinic.entity.Doctor;
import com.domaszekkk.medicalclinic.entity.Patient;
import com.domaszekkk.medicalclinic.entity.Visit;
import com.domaszekkk.medicalclinic.exception.*;
import com.domaszekkk.medicalclinic.mapper.VisitMapper;
import com.domaszekkk.medicalclinic.repository.DoctorJpaRepository;
import com.domaszekkk.medicalclinic.repository.PatientJpaRepository;
import com.domaszekkk.medicalclinic.repository.VisitJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitJpaRepository visitJpaRepository;
    private final DoctorJpaRepository doctorJpaRepository;
    private final VisitMapper visitMapper;
    private final PatientJpaRepository patientJpaRepository;

    public VisitDto addVisit(Long doctorId, AddVisitCommand command) {
        Doctor doctor = doctorJpaRepository
                .findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        validateVisitDate(doctorId, command.getDateTime());

        Visit visit = visitMapper.mapToEntity(command);
        visit.setDoctor(doctor);
        return visitMapper.mapToDto(visitJpaRepository.save(visit));
    }

    private void validateVisitDate(Long doctorId, LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidVisitDateException("Cannot create visit in the past");
        }

        if (dateTime.getMinute() % 15 != 0) {
            throw new InvalidVisitDateException("Visit must be at full quarter of an hour");
        }

        if (visitJpaRepository.existsByDoctorIdAndDateTime(doctorId, dateTime)) {
            throw new DoctorVisitConflictException(dateTime);
        }
    }

    public VisitDto registerPatientForVisit(Long visitId, Long patientId) {
        Visit visit = visitJpaRepository
                .findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException(visitId));

        Patient patient = patientJpaRepository
                .findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId.toString()));

        validatePatientRegistration(visit);

        visit.setPatient(patient);
        return visitMapper.mapToDto(visitJpaRepository.save(visit));
    }

    private void validatePatientRegistration(Visit visit) {
        if (visit.getPatient() != null) {
            throw new VisitAlreadyTakenException(visit.getId());
        }

        if (visit.getDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidVisitDateException("cannot register for past visit");
        }
    }

    public List<VisitDto> getPatientVisits(Long patientId) {
        return visitMapper.mapToDtoList(visitJpaRepository.findByPatientId(patientId));
    }
}
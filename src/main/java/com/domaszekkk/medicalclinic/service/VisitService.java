package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.VisitDto;
import com.domaszekkk.medicalclinic.entity.Doctor;
import com.domaszekkk.medicalclinic.entity.Patient;
import com.domaszekkk.medicalclinic.entity.Visit;
import com.domaszekkk.medicalclinic.exception.DoctorNotFoundException;
import com.domaszekkk.medicalclinic.exception.PatientNotFoundException;
import com.domaszekkk.medicalclinic.exception.VisitNotFoundException;
import com.domaszekkk.medicalclinic.mapper.VisitMapper;
import com.domaszekkk.medicalclinic.repository.DoctorJpaRepository;
import com.domaszekkk.medicalclinic.repository.PatientJpaRepository;
import com.domaszekkk.medicalclinic.repository.VisitJpaRepository;
import com.domaszekkk.medicalclinic.validator.VisitValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitJpaRepository visitJpaRepository;
    private final DoctorJpaRepository doctorJpaRepository;
    private final PatientJpaRepository patientJpaRepository;
    private final VisitMapper visitMapper;
    private final VisitValidator visitValidator;

    public VisitDto addVisit(Long doctorId, AddVisitCommand command) {
        Doctor doctor = doctorJpaRepository
                .findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        visitValidator.validateVisitDate(doctorId, command.getStartDateTime(), command.getEndDateTime());

        Visit visit = visitMapper.mapToEntity(command);
        visit.setDoctor(doctor);
        return visitMapper.mapToDto(visitJpaRepository.save(visit));
    }

    public VisitDto registerPatientForVisit(Long visitId, Long patientId) {
        Visit visit = visitJpaRepository
                .findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException(visitId));

        Patient patient = patientJpaRepository
                .findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId.toString()));

        visitValidator.validatePatientRegistration(visit);

        visit.setPatient(patient);
        return visitMapper.mapToDto(visitJpaRepository.save(visit));
    }

    public List<VisitDto> getPatientVisits(Long patientId) {
        return visitMapper.mapToDtoList(visitJpaRepository.findByPatientId(patientId));
    }
}
package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddPatientRequest;
import com.domaszekkk.medicalclinic.dto.PatientDto;
import com.domaszekkk.medicalclinic.dto.UpdatePatientRequest;
import com.domaszekkk.medicalclinic.exception.PatientNotFoundException;
import com.domaszekkk.medicalclinic.entity.Patient;
import com.domaszekkk.medicalclinic.mapper.PatientMapper;
import com.domaszekkk.medicalclinic.repository.PatientJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientJpaRepository patientJpaRepository;
    private final PatientMapper patientMapper;

    public List<PatientDto> getAllPatients() {
        return patientMapper.mapToDtoList(patientJpaRepository.findAll());
    }

    public PatientDto addPatient(AddPatientRequest request) {
        Patient patient = patientMapper.mapToEntity(request);
        return patientMapper.mapToDto(patientJpaRepository.save(patient));
    }

    public PatientDto getPatientByEmail(String email) {
        Patient patient = patientJpaRepository
                .findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        return patientMapper.mapToDto(patient);
    }

    public void deletePatientByEmail(String email) {
        patientJpaRepository.deleteByUserEmail(email);
    }

    public void updatePatient(String email, UpdatePatientRequest request) {
        Patient patient = patientJpaRepository
                .findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.update(patientMapper.mapToEntity(request));
        patientJpaRepository.save(patient);
    }

    public void updatePassword(String email, String password) {
        Patient patient = patientJpaRepository
                .findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.getUser().setPassword(password);
        patientJpaRepository.save(patient);
    }
}
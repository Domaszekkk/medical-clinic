package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddPatientCommand;
import com.domaszekkk.medicalclinic.dto.PatientDto;
import com.domaszekkk.medicalclinic.dto.UpdatePatientRequest;
import com.domaszekkk.medicalclinic.entity.User;
import com.domaszekkk.medicalclinic.exception.PatientNotFoundException;
import com.domaszekkk.medicalclinic.entity.Patient;
import com.domaszekkk.medicalclinic.exception.UserNotFoundException;
import com.domaszekkk.medicalclinic.mapper.PatientMapper;
import com.domaszekkk.medicalclinic.repository.PatientJpaRepository;
import com.domaszekkk.medicalclinic.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientJpaRepository patientJpaRepository;
    private final PatientMapper patientMapper;
    private final UserJpaRepository userJpaRepository;

    public List<PatientDto> getAllPatients() {
        return patientMapper.mapToDtoList(patientJpaRepository.findAll());
    }

    public PatientDto addPatient(AddPatientCommand command) {
        User user = userJpaRepository
                .findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException(command.getUserId().toString()));
        Patient patient = patientMapper.mapToEntity(command);
        patient.setUser(user);
        return patientMapper.mapToDto(patientJpaRepository.save(patient));
    }

    public PatientDto getPatientByEmail(String email) {
        Patient patient = patientJpaRepository
                .findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        return patientMapper.mapToDto(patient);
    }
    @Transactional
    public void deletePatientByEmail(String email) {
        patientJpaRepository.deleteByUserEmail(email);
    }

    public PatientDto updatePatient(String email, UpdatePatientRequest request) {
        Patient patient = patientJpaRepository
                .findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.update(patientMapper.mapToEntity(request));
        patientJpaRepository.save(patient);
        return patientMapper.mapToDto(patient);
    }

    public void updatePassword(String email, String password) {
        Patient patient = patientJpaRepository
                .findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.getUser().setPassword(password);
        patientJpaRepository.save(patient);
    }
}
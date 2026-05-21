package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddPatientRequest;
import com.domaszekkk.medicalclinic.dto.PatientDto;
import com.domaszekkk.medicalclinic.dto.UpdatePatientRequest;
import com.domaszekkk.medicalclinic.exception.PatientNotFoundException;
import com.domaszekkk.medicalclinic.entity.Patient;
import com.domaszekkk.medicalclinic.mapper.PatientMapper;
import com.domaszekkk.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public List<PatientDto> getAllPatients() {
        return patientMapper.mapToDtoList(patientRepository.getAllPatients());
    }

    public PatientDto addPatient(AddPatientRequest request) {
        Patient patient = patientMapper.mapToEntity(request);
        Patient addedPatient = patientRepository.addPatient(patient);

        return patientMapper.mapToDto(addedPatient);
    }

    public PatientDto getPatientByEmail(String email) {
        Patient patient = patientRepository
                .getPatientByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        return patientMapper.mapToDto(patient);
    }

    public void deletePatientByEmail(String email) {
        patientRepository.deletePatientByEmail(email);
    }

    public void updatePatient(String email, UpdatePatientRequest request) {
        Patient patient = patientMapper.mapToEntity(request);
        patientRepository.updatePatient(email, patient);
    }

    public void updatePassword(String email, String password) {
        patientRepository.updatePassword(email, password);
    }
}
package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.model.Patient;
import com.domaszekkk.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.getAllPatients();
    }

    public Patient addPatient(Patient patient) {
        return patientRepository.addPatient(patient);
    }

    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.getPatientByEmail(email);
    }

    public void deletePatientByEmail(String email) {
        patientRepository.deletePatientByEmail(email);
    }

    public void updatePatient(String email, Patient updatedPatient) {
        patientRepository.updatePatient(email, updatedPatient);
    }
}
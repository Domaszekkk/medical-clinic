package com.domaszekkk.medicalclinic.repository;

import com.domaszekkk.medicalclinic.exception.PatientAlreadyExistsException;
import com.domaszekkk.medicalclinic.exception.PatientNotFoundException;
import com.domaszekkk.medicalclinic.entity.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {
    private final List<Patient> patients = new ArrayList<>();

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public Patient addPatient(Patient patient) {
        if (getPatientByEmail(patient.getEmail()).isPresent()) {
            throw new PatientAlreadyExistsException(patient.getEmail());
        }
        patients.add(patient);
        return patient;
    }

    public Optional<Patient> getPatientByEmail(String email) {
        return patients.stream()
                .filter(patient -> email.equalsIgnoreCase(patient.getEmail()))
                .findFirst();
    }

    public void deletePatientByEmail(String email) {
        patients.removeIf(patient -> patient.getEmail().equals(email));
    }

    public void updatePatient(String email, Patient updatedPatient) {
        Patient patient = getPatientByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.update(updatedPatient);
    }

    public void updatePassword(String email ,String password) {
        Patient patient = getPatientByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.setPassword(password);
    }
}
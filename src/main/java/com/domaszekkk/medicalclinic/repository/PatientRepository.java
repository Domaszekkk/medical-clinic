package com.domaszekkk.medicalclinic.repository;

import com.domaszekkk.medicalclinic.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {
    private final List<Patient> patients = new ArrayList<>();

    public List<Patient> getAllPatients() {
        return patients;
    }

    public Patient addPatient(Patient patient) {
        patients.add(patient);
        return patient;
    }

    public Optional<Patient> getPatientByEmail(String email) {
        for (Patient patient : patients) {
            if (patient.getEmail().equals(email)) {
                return Optional.of(patient);
            }
        }
        return Optional.empty();
    }

    public void deletePatientByEmail(String email) {
        patients.removeIf(patient -> patient.getEmail().equals(email));
    }

    public void updatePatient(String email, Patient updatedPatient) {
        getPatientByEmail(email)
                .ifPresent(patient -> {
                    patient.setFirstName(updatedPatient.getFirstName());
                    patient.setLastName(updatedPatient.getLastName());
                    patient.setPhoneNumber(updatedPatient.getPhoneNumber());
                    patient.setBirthday(updatedPatient.getBirthday());
                    patient.setPassword(updatedPatient.getPassword());
                    patient.setIdCardNo(updatedPatient.getIdCardNo());
                    patient.setEmail(updatedPatient.getEmail());
                });
    }
}
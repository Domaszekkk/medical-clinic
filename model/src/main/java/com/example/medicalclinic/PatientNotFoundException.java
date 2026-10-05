package com.example.medicalclinic;

public class PatientNotFoundException extends MedicalClinicException {
    public PatientNotFoundException(Long id) {
        super("Patient with id " + id + " not found", 404);
    }

    public PatientNotFoundException(String email) {
        super("Patient with email " + email + " not found", 404);
    }
}

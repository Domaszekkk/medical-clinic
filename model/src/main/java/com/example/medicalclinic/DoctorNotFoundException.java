package com.example.medicalclinic;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException(Long id) {
        super("Doctor with id " + id + " not found", 404);
    }
}

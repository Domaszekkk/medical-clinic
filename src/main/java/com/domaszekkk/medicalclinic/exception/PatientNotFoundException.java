package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedicalClinicException {
    public PatientNotFoundException(String email) {
        super("Patient with email " + email + " not found", HttpStatus.NOT_FOUND);
    }
}
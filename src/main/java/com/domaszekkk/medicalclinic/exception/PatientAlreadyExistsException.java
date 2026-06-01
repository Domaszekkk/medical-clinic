package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class PatientAlreadyExistsException extends MedicalClinicException {
    public PatientAlreadyExistsException(String email) {
        super("Patient with email " + email + " already exists", HttpStatus.CONFLICT);
    }
}
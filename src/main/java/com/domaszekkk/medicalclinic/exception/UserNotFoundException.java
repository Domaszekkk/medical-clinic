package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends MedicalClinicException {
    public UserNotFoundException(String email) {
        super("User with email " + email + " not found", HttpStatus.NOT_FOUND);
    }
}
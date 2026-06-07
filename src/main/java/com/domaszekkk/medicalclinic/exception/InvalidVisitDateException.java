package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class InvalidVisitDateException extends MedicalClinicException {
    public InvalidVisitDateException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

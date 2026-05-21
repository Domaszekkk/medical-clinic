package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PatientExceptionHandler {

    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<ErrorMessage> handleMedicalClinicException(MedicalClinicException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(new ErrorMessage(exception.getMessage()));
    }
}
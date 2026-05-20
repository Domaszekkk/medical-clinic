package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PatientExceptionHandler {

    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handlePatientNotFoundException(PatientNotFoundException exception) {
        return new ErrorMessage(exception.getMessage());
    }

    @ExceptionHandler(PatientAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handlePatientAlreadyExistsException(PatientAlreadyExistsException exception) {
        return new ErrorMessage(exception.getMessage()
        );
    }
}


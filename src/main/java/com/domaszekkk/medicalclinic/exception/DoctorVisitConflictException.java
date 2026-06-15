package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class DoctorVisitConflictException extends MedicalClinicException {
    public DoctorVisitConflictException(LocalDateTime dateTime) {
        super("Doctor already has a visit at " + dateTime, HttpStatus.CONFLICT);
    }
}
package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class VisitNotFoundException extends MedicalClinicException {
    public VisitNotFoundException(Long visitId) {
        super("Visit with id " + visitId + " not found", HttpStatus.NOT_FOUND);
    }
}
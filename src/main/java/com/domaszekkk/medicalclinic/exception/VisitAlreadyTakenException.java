package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class VisitAlreadyTakenException extends MedicalClinicException {
    public VisitAlreadyTakenException(Long visitId) {
        super("visit with id " + visitId + " is already taken", HttpStatus.CONFLICT);
    }
}
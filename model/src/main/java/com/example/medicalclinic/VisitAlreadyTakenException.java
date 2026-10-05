package com.example.medicalclinic;

public class VisitAlreadyTakenException extends MedicalClinicException {
    public VisitAlreadyTakenException(Long visitId) {
        super("Visit with id " + visitId + " is already taken", 409);
    }
}

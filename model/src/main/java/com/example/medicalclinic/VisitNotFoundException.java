package com.example.medicalclinic;

public class VisitNotFoundException extends MedicalClinicException {
    public VisitNotFoundException(Long visitId) {
        super("Visit with id " + visitId + " not found", 404);
    }
}

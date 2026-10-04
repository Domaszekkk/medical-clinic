package com.example.medicalclinic;


public class FacilityNotFoundException extends MedicalClinicException {
    public FacilityNotFoundException(Long id) {
        super("Facility with id " + id + " not found", 404);
    }
}
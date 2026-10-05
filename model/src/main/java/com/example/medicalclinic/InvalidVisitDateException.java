package com.example.medicalclinic;

public class InvalidVisitDateException extends MedicalClinicException {
    public InvalidVisitDateException(String message) {
        super(message, 400);
    }
}

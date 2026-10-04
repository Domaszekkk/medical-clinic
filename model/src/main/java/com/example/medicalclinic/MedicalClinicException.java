package com.example.medicalclinic;

import lombok.Getter;

@Getter
public class MedicalClinicException extends RuntimeException {
    private final Integer responseCode;

    public MedicalClinicException(String message, Integer responseCode) {
        super(message);
        this.responseCode = responseCode;
    }
}
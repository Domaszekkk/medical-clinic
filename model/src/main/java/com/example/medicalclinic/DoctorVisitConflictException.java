package com.example.medicalclinic;

import java.time.LocalDateTime;

public class DoctorVisitConflictException extends MedicalClinicException {
    public DoctorVisitConflictException(LocalDateTime dateTime) {
        super("Doctor already has a visit at " + dateTime, 409);
    }
}

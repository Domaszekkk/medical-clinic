package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class DoctorNotAssignedToFacilityException extends MedicalClinicException {
    public DoctorNotAssignedToFacilityException(Long doctorId, Long facilityId) {
        super("Doctor with id " + doctorId + " is not assigned to facility with id " + facilityId, HttpStatus.CONFLICT);
    }
}
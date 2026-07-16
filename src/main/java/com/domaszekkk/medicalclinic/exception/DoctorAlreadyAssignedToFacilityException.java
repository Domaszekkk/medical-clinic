package com.domaszekkk.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class DoctorAlreadyAssignedToFacilityException extends MedicalClinicException {
    public DoctorAlreadyAssignedToFacilityException(Long doctorId, Long facilityId) {
        super("Doctor with id " + doctorId + " is already assigned to facility with id " + facilityId, HttpStatus.CONFLICT);
    }
}
package com.example.medicalclinic;

public class DoctorNotAssignedToFacilityException extends MedicalClinicException {
    public DoctorNotAssignedToFacilityException(Long doctorId, Long facilityId) {
        super("Doctor with id " + doctorId + " is not assigned to facility with id " + facilityId, 409);
    }
}

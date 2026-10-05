package com.example.medicalclinic;

public class UserNotFoundException extends MedicalClinicException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found", 404);
    }

    public UserNotFoundException(String email) {
        super("User with email " + email + " not found", 404);
    }
}

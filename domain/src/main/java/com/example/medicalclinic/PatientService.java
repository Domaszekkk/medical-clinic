package com.example.medicalclinic;


import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PatientService {
    private final PatientJpaRepositoryPort patientJpaRepositoryPort;
    private final UserJpaRepositoryPort userJpaRepositoryPort;

    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientJpaRepositoryPort.findAll(pageable);
    }

    public Patient addPatient(AddPatientCommand command) {
        User user = userJpaRepositoryPort
                .findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException(command.getUserId()));
        Patient patient = Patient.from(command, user);
        return patientJpaRepositoryPort.save(patient);
    }

    public List<Patient> getPatientByEmail(String email) {
        return patientJpaRepositoryPort
                .findByUserEmail(email);
    }
    public void deletePatientByEmail(String email) {
        patientJpaRepositoryPort.deleteByUserEmail(email);
    }

    public Patient updatePatient(Long id, UpdatePatientCommand command) {
        Patient patient = patientJpaRepositoryPort
                .findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        patient.updateFrom(command);
        return patientJpaRepositoryPort.save(patient);
    }
}
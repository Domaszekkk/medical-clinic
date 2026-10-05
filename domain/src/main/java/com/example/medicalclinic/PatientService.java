package com.example.medicalclinic;

import lombok.RequiredArgsConstructor;

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
        Patient patient = Patient.from(command);
        patient.setUser(user);
        return patientJpaRepositoryPort.save(patient);
    }

    public Patient getPatientByEmail(String email) {
        return patientJpaRepositoryPort
                .findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
    }

    public Patient updatePatient(String email, UpdatePatientRequest request) {
        Patient patient = patientJpaRepositoryPort
                .findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.update(request);
        return patientJpaRepositoryPort.save(patient);
    }

    public void updatePassword(String email, String password) {
        Patient patient = patientJpaRepositoryPort
                .findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.getUser().setPassword(password);
        patientJpaRepositoryPort.save(patient);
    }

    public void deletePatientByEmail(String email) {
        patientJpaRepositoryPort.deleteByUserEmail(email);
    }
}

package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.command.ChangePasswordCommand;
import com.domaszekkk.medicalclinic.model.Patient;
import com.domaszekkk.medicalclinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient addPatient(@RequestBody Patient patient) {
        return patientService.addPatient(patient);
    }

    @GetMapping("/{email}")
    public Patient getPatientByEmail(
            @PathVariable String email
    ) {
        return patientService.getPatientByEmail(email);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatientByEmail(
            @PathVariable String email
    ) {
        patientService.deletePatientByEmail(email);
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePatient(
            @PathVariable String email,
            @RequestBody Patient patient
    ) {
        patientService.updatePatient(email, patient);
    }

    @PatchMapping("/{email}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@PathVariable String email, @RequestBody ChangePasswordCommand command) {
        patientService.updatePassword(email, command.getPassword());
    }
}
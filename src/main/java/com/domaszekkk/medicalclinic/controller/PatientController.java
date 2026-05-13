package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.model.Patient;
import com.domaszekkk.medicalclinic.service.PatientService;
import lombok.RequiredArgsConstructor;
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
    public void deletePatientByEmail(
            @PathVariable String email
    ) {
        patientService.deletePatientByEmail(email);
    }

    @PutMapping("/{email}")
    public void updatePatient(
            @PathVariable String email,
            @RequestBody Patient patient
    ) {
        patientService.updatePatient(email, patient);
    }
}
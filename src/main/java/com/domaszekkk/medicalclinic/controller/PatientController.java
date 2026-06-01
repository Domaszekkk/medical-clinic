package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.ChangePasswordCommand;
import com.domaszekkk.medicalclinic.dto.AddPatientCommand;
import com.domaszekkk.medicalclinic.dto.PatientDto;
import com.domaszekkk.medicalclinic.dto.UpdatePatientRequest;
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
    public List<PatientDto> getAllPatients() {
        return patientService.getAllPatients();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody AddPatientCommand request) {
        return patientService.addPatient(request);
    }

    @GetMapping("/{email}")
    public PatientDto getPatientByEmail(@PathVariable String email) {
        return patientService.getPatientByEmail(email);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatientByEmail(@PathVariable String email) {
        patientService.deletePatientByEmail(email);
    }

    @PutMapping("/{email}")
    public PatientDto updatePatient(@PathVariable String email, @RequestBody UpdatePatientRequest request) {
        return patientService.updatePatient(email, request);
    }

    @PatchMapping("/{email}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@PathVariable String email, @RequestBody ChangePasswordCommand command) {
        patientService.updatePassword(email, command.getPassword());
    }
}
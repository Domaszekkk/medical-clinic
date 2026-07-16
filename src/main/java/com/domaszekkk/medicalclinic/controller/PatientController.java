package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.*;
import com.domaszekkk.medicalclinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "Endpoints for managing patients")
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Get all patients", description = "Returns a paginated list of all patients")
    @GetMapping
    public PageResponse<PatientDto> getAllPatients(Pageable pageable) {
        return PageResponse.of(patientService.getAllPatients(pageable));
    }

    @Operation(summary = "Create a new patient", description = "Creates a new patient linked to an existing user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody AddPatientCommand request) {
        return patientService.addPatient(request);
    }

    @Operation(summary = "Get patient by email", description = "Returns a single patient identified by their user email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/{email}")
    public PatientDto getPatientByEmail(@Parameter(description = "Email of the patient to retrieve") @PathVariable String email) {
        return patientService.getPatientByEmail(email);
    }

    @Operation(summary = "Delete patient", description = "Deletes a patient identified by their user email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully")
    })
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatientByEmail(@Parameter(description = "Email of the patient to delete") @PathVariable String email) {
        patientService.deletePatientByEmail(email);
    }

    @Operation(summary = "Update patient", description = "Updates the personal data of an existing patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PutMapping("/{email}")
    public PatientDto updatePatient(@Parameter(description = "Email of the patient to update") @PathVariable String email, @RequestBody UpdatePatientRequest request) {
        return patientService.updatePatient(email, request);
    }

    @Operation(summary = "Change patient password", description = "Updates the password of the user account linked to the patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password updated successfully")
    })
    @PatchMapping("/{email}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@Parameter(description = "Email of the patient whose password is being changed") @PathVariable String email, @RequestBody ChangePasswordCommand command) {
        patientService.updatePassword(email, command.getPassword());
    }
}
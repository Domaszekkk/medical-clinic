package com.example.medicalclinic.patient;

import com.example.medicalclinic.AddPatientCommand;
import com.example.medicalclinic.ChangePasswordCommand;
import com.example.medicalclinic.Page;
import com.example.medicalclinic.PageResponse;
import com.example.medicalclinic.Pageable;
import com.example.medicalclinic.Patient;
import com.example.medicalclinic.PatientDto;
import com.example.medicalclinic.PatientService;
import com.example.medicalclinic.UpdatePatientRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "Endpoints for managing patients")
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @Operation(summary = "Get all patients", description = "Returns a paginated list of all patients")
    @GetMapping
    public PageResponse<PatientDto> getAllPatients(org.springframework.data.domain.Pageable pageable) {
        Page<Patient> page = patientService.getAllPatients(toModelPageable(pageable));
        List<PatientDto> content = page.getContent().stream()
                .map(patientMapper::toDto)
                .toList();
        return PageResponse.<PatientDto>builder()
                .content(content)
                .pageNumber(page.getPageNumber())
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Operation(summary = "Create a new patient", description = "Creates a new patient linked to an existing user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody AddPatientCommand request) {
        return patientMapper.toDto(patientService.addPatient(request));
    }

    @Operation(summary = "Get patient by email", description = "Returns a single patient identified by their user email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/{email}")
    public PatientDto getPatientByEmail(@Parameter(description = "Email of the patient to retrieve") @PathVariable String email) {
        return patientMapper.toDto(patientService.getPatientByEmail(email));
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
    public PatientDto updatePatient(@Parameter(description = "Email of the patient to update") @PathVariable String email,
                                    @RequestBody UpdatePatientRequest request) {
        return patientMapper.toDto(patientService.updatePatient(email, request));
    }

    @Operation(summary = "Change patient password", description = "Updates the password of the user account linked to the patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password updated successfully")
    })
    @PatchMapping("/{email}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@Parameter(description = "Email of the patient whose password is being changed") @PathVariable String email,
                               @RequestBody ChangePasswordCommand command) {
        patientService.updatePassword(email, command.getPassword());
    }

    private Pageable toModelPageable(org.springframework.data.domain.Pageable pageable) {
        return Pageable.builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .build();
    }
}

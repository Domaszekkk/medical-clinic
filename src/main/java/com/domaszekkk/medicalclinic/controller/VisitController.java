package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.PageResponse;
import com.domaszekkk.medicalclinic.dto.VisitDto;
import com.domaszekkk.medicalclinic.service.VisitService;
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
@RequiredArgsConstructor
@Tag(name = "Visit Management", description = "Endpoints for managing visits")
public class VisitController {
    private final VisitService visitService;

    @Operation(summary = "Create a visit",
            description = "Creates a new available visit slot for a doctor at a given facility")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Visit created successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor or facility not found"),
            @ApiResponse(responseCode = "409", description = "Doctor is not assigned to the facility, or has a conflicting visit")
    })
    @PostMapping("/doctors/{doctorId}/visits")
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDto addVisit(@Parameter(description = "Id of the doctor") @PathVariable Long doctorId, @RequestBody AddVisitCommand command) {
        return visitService.addVisit(doctorId, command);
    }

    @Operation(summary = "Register patient for visit",
            description = "Assigns a patient to an existing available visit slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient registered for visit successfully"),
            @ApiResponse(responseCode = "404", description = "Visit or patient not found"),
            @ApiResponse(responseCode = "409", description = "Visit is already taken")
    })
    @PatchMapping("/visits/{visitId}")
    public VisitDto registerPatientForVisit(
            @Parameter(description = "Id of the visit") @PathVariable Long visitId,
            @Parameter(description = "Id of the patient to register") @RequestParam Long patientId) {
        return visitService.registerPatientForVisit(visitId, patientId);
    }

    @Operation(summary = "Get patient visits", description = "Returns a paginated list of visits for a given patient")
    @GetMapping("/patients/{patientId}/visits")
    public PageResponse<VisitDto> getPatientVisits(@Parameter(description = "Id of the patient") @PathVariable Long patientId, Pageable pageable) {
        return PageResponse.of(visitService.getPatientVisits(patientId, pageable));
    }

    @Operation(summary = "Get available visits",
            description = "Returns a paginated list of visit slots that do not have a patient assigned yet")
    @GetMapping("/visits/available")
    public PageResponse<VisitDto> getAvailableVisits(Pageable pageable) {
        return PageResponse.of(visitService.getAvailableVisits(pageable));
    }
}
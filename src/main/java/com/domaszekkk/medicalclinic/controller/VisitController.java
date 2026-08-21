package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.PageResponse;
import com.domaszekkk.medicalclinic.dto.VisitDto;
import com.domaszekkk.medicalclinic.dto.VisitScope;
import com.domaszekkk.medicalclinic.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    @PutMapping("/visits/{visitId}")
    public VisitDto registerPatientForVisit(
            @Parameter(description = "Id of the visit") @PathVariable Long visitId,
            @Parameter(description = "Id of the patient to register") @RequestParam Long patientId) {
        return visitService.registerPatientForVisit(visitId, patientId);
    }

    @Operation(summary = "Get patient visits", description = "Returns a paginated list of visits for a given patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
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

    @Operation(summary = "Get available visits in a time range",
            description = "Returns a paginated list of available visit slots within a given time range, optionally filtered by doctor specialization")
    @GetMapping("/visits/available/search")
    public PageResponse<VisitDto> getAvailableVisitsInRange(
            @Parameter(description = "Start of the time range (inclusive)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "End of the time range (exclusive)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @Parameter(description = "Doctor specialization to filter by")
            @RequestParam(required = false) String specialization,
            Pageable pageable) {
        return PageResponse.of(visitService.getAvailableVisitsInRange(from, to, specialization, pageable));
    }

    @Operation(summary = "Get visits in a time range",
            description = "Returns all visits (taken and available) within a given time range for a given doctor specialization")
    @GetMapping("/visits")
    public PageResponse<VisitDto> getVisitsInRange(
            @Parameter(description = "Start of the time range (inclusive)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "End of the time range (exclusive)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @Parameter(description = "Doctor specialization to filter by")
            @RequestParam String specialization,
            Pageable pageable) {
        return PageResponse.of(visitService.getVisitsInRange(from, to, specialization, pageable));
    }

    @Operation(summary = "Get available visits for a doctor",
            description = "Returns a paginated list of visit slots for a given doctor that do not have a patient assigned yet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/doctors/{doctorId}/visits/available")
    public PageResponse<VisitDto> getDoctorAvailableVisits(
            @Parameter(description = "Id of the doctor") @PathVariable Long doctorId, Pageable pageable) {
        return PageResponse.of(visitService.getDoctorAvailableVisits(doctorId, pageable));
    }

    @Operation(summary = "Get doctor visits",
            description = "Returns a paginated list of visits for a given doctor, filtered by scope: PAST, UPCOMING or ALL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/doctors/{doctorId}/visits")
    public PageResponse<VisitDto> getDoctorVisits(
            @Parameter(description = "Id of the doctor") @PathVariable Long doctorId,
            @Parameter(description = "Scope of visits to return") @RequestParam(defaultValue = "ALL") VisitScope scope,
            Pageable pageable) {
        return PageResponse.of(visitService.getDoctorVisits(doctorId, scope, pageable));
    }

    @Operation(summary = "Cancel a visit",
            description = "Cancels (deletes) a visit slot belonging to a given doctor, regardless of whether a patient was assigned")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Visit cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Visit not found for this doctor")
    })
    @DeleteMapping("/doctors/{doctorId}/visits/{visitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelVisit(
            @Parameter(description = "Id of the doctor") @PathVariable Long doctorId,
            @Parameter(description = "Id of the visit to cancel") @PathVariable Long visitId) {
        visitService.cancelVisit(doctorId, visitId);
    }
}
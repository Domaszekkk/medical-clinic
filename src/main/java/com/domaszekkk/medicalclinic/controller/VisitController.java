package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.*;
import com.domaszekkk.medicalclinic.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
    @PutMapping("/visits/{visitId}")
    public VisitDto registerPatientForVisit(
            @Parameter(description = "Id of the visit") @PathVariable Long visitId,
            @Parameter(description = "Id of the patient to register") @RequestParam Long patientId) {
        return visitService.registerPatientForVisit(visitId, patientId);
    }

    @Operation(summary = "Get visits",
            description = "Returns a paginated, filterable list of visits. All filters are optional and can be combined: " +
                    "filter by patient, by doctor, by a time range, by doctor specialization, by availability (no patient assigned), " +
                    "or by scope relative to now (PAST, UPCOMING, ALL).")
    @ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Patient or doctor not found")})
    @GetMapping("/visits")
    public PageResponse<VisitDto> getVisits(@ParameterObject VisitFilter filter, Pageable pageable) {
        return PageResponse.of(visitService.getVisits(filter, pageable));
    }

    @Operation(summary = "Cancel a visit",
            description = "Cancels (deletes) a visit slot. If doctorId is provided, the visit must belong to that doctor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Visit cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Visit not found (or not found for this doctor)")
    })
    @DeleteMapping("/visits/{visitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelVisit(
            @Parameter(description = "Id of the visit to cancel") @PathVariable Long visitId,
            @Parameter(description = "Id of the doctor who owns the visit") @RequestParam(required = false) Long doctorId) {
        visitService.cancelVisit(visitId, doctorId);
    }
}
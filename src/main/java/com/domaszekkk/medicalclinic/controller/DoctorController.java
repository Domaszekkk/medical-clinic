package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.AddDoctorCommand;
import com.domaszekkk.medicalclinic.dto.DoctorDto;
import com.domaszekkk.medicalclinic.dto.PageResponse;
import com.domaszekkk.medicalclinic.service.DoctorService;
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
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "Endpoints for managing doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Get all doctors", description = "Returns a paginated list of all doctors")
    @GetMapping
    public PageResponse<DoctorDto> getAllDoctors(Pageable pageable) {
        return PageResponse.of(doctorService.getAllDoctors(pageable));
    }

    @Operation(summary = "Get doctor by id", description = "Returns a single doctor identified by their id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/{id}")
    public DoctorDto getDoctorById(@Parameter(description = "Id of the doctor to retrieve") @PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @Operation(summary = "Create a new doctor", description = "Creates a new doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created successfully")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto addDoctor(@RequestBody AddDoctorCommand command) {
        return doctorService.addDoctor(command);
    }

    @Operation(summary = "Update doctor", description = "Updates the data of an existing doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PutMapping("/{id}")
    public DoctorDto updateDoctor(@Parameter(description = "Id of the doctor to update") @PathVariable Long id, @RequestBody AddDoctorCommand command) {
        return doctorService.updateDoctor(id, command);
    }

    @Operation(summary = "Delete doctor", description = "Deletes a doctor by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor deleted successfully")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoctor(@Parameter(description = "Id of the doctor to delete") @PathVariable Long id) {
        doctorService.deleteDoctor(id);
    }

    @Operation(summary = "Assign doctor to facility", description = "Assigns an existing doctor to work at a given facility")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor assigned to facility successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor or facility not found"),
            @ApiResponse(responseCode = "409", description = "Doctor is already assigned to this facility")
    })
    @PostMapping("/{doctorId}/facilities/{facilityId}")
    public DoctorDto assignDoctorToFacility(
            @Parameter(description = "Id of the doctor") @PathVariable Long doctorId,
            @Parameter(description = "Id of the facility") @PathVariable Long facilityId) {
        return doctorService.assignDoctorToFacility(doctorId, facilityId);
    }
}
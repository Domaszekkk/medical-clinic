package com.example.medicalclinic.facility;

import com.example.medicalclinic.AddFacilityCommand;
import com.example.medicalclinic.Facility;
import com.example.medicalclinic.FacilityDto;
import com.example.medicalclinic.FacilityService;
import com.example.medicalclinic.Page;
import com.example.medicalclinic.PageFactory;
import com.example.medicalclinic.PageResponse;
import com.example.medicalclinic.PageResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
@Tag(name = "Facility Management", description = "Endpoints for managing medical facilities")
public class FacilityController {
    private final FacilityService facilityService;
    private final FacilityMapper facilityMapper;

    @Operation(summary = "Get all facilities", description = "Returns a paginated list of all facilities")
    @GetMapping
    public PageResponse<FacilityDto> getAllFacilities(org.springframework.data.domain.Pageable pageable) {
        Page<Facility> page = facilityService.getAllFacilities(PageFactory.toPageable(pageable));
        return PageResponseFactory.from(page, facilityMapper::toDto);
    }

    @Operation(summary = "Get facility by id", description = "Returns a single facility identified by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility found"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @GetMapping("/{id}")
    public FacilityDto getFacilityById(@Parameter(description = "Id of the facility to retrieve") @PathVariable Long id) {
        return facilityMapper.toDto(facilityService.getFacilityById(id));
    }

    @Operation(summary = "Create a new facility", description = "Creates a new medical facility")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Facility created successfully")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityDto addFacility(@RequestBody AddFacilityCommand command) {
        return facilityMapper.toDto(facilityService.addFacility(command));
    }

    @Operation(summary = "Update facility", description = "Updates the data of an existing facility")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility updated successfully"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @PutMapping("/{id}")
    public FacilityDto updateFacility(@Parameter(description = "Id of the facility to update") @PathVariable Long id,
                                      @RequestBody AddFacilityCommand command) {
        return facilityMapper.toDto(facilityService.updateFacility(id, command));
    }

    @Operation(summary = "Delete facility", description = "Deletes a facility by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Facility deleted successfully")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFacility(@Parameter(description = "Id of the facility to delete") @PathVariable Long id) {
        facilityService.deleteFacility(id);
    }
}

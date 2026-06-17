package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.AddFacilityCommand;
import com.domaszekkk.medicalclinic.dto.FacilityDto;
import com.domaszekkk.medicalclinic.dto.PageResponse;
import com.domaszekkk.medicalclinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @GetMapping
    public PageResponse<FacilityDto> getAllFacilities(Pageable pageable) {
        return PageResponse.of(facilityService.getAllFacilities(pageable));
    }

    @GetMapping("/{id}")
    public FacilityDto getFacilityById(@PathVariable Long id) {
        return facilityService.getFacilityById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityDto addFacility(@RequestBody AddFacilityCommand command) {
        return facilityService.addFacility(command);
    }

    @PutMapping("/{id}")
    public FacilityDto updateFacility(@PathVariable Long id, @RequestBody AddFacilityCommand command) {
        return facilityService.updateFacility(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFacility(@PathVariable Long id) {
        facilityService.deleteFacility(id);
    }
}
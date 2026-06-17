package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.PageResponse;
import com.domaszekkk.medicalclinic.dto.VisitDto;
import com.domaszekkk.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @PostMapping("/doctors/{doctorId}/visits")
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDto addVisit(@PathVariable Long doctorId, @RequestBody AddVisitCommand command) {
        return visitService.addVisit(doctorId, command);
    }

    @PatchMapping("/visits/{visitId}")
    public VisitDto registerPatientForVisit(@PathVariable Long visitId, @RequestParam Long patientId) {
        return visitService.registerPatientForVisit(visitId, patientId);
    }

    @GetMapping("/patients/{patientId}/visits")
    public PageResponse<VisitDto> getPatientVisits(@PathVariable Long patientId, Pageable pageable) {
        return PageResponse.of(visitService.getPatientVisits(patientId, pageable));
    }
}
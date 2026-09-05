package com.domaszekkk.medicalclinic.dto;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record VisitFilter(
        @Parameter(description = "Filter by patient id")
        Long patientId,

        @Parameter(description = "Filter by doctor id")
        Long doctorId,

        @Parameter(description = "Start of the time range (inclusive)")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime from,

        @Parameter(description = "End of the time range (exclusive)")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime to,

        @Parameter(description = "Filter by doctor specialization")
        String specialization,

        @Parameter(description = "If true, return only visits without a patient assigned")
        Boolean available,

        @Parameter(description = "Filter by time scope relative to now: PAST, UPCOMING or ALL")
        VisitScope scope
) {
}
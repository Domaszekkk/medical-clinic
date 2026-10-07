package com.example.medicalclinic;

import java.time.LocalDateTime;

public record VisitFilter(
        Long patientId,
        Long doctorId,
        LocalDateTime from,
        LocalDateTime to,
        String specialization,
        Boolean available,
        VisitScope scope
) {
}

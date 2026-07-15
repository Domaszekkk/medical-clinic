package com.domaszekkk.medicalclinic.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisitDto {
    private Long id;
    private LocalDateTime startDateTime;
    private Long doctorId;
    private Long patientId;
    private Long facilityId;
    private LocalDateTime endDateTime;
}

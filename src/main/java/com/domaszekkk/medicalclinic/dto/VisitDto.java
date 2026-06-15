package com.domaszekkk.medicalclinic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VisitDto {
    private Long Id;
    private LocalDateTime startDateTime;
    private Long doctorId;
    private Long patientId;
    private LocalDateTime endDateTime;
}

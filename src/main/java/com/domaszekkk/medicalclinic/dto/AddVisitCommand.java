package com.domaszekkk.medicalclinic.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddVisitCommand {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long facilityId;
}

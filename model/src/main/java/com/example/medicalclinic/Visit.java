package com.example.medicalclinic;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Visit {
    private Long id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Doctor doctor;
    private Patient patient;
    private Facility facility;

    public static Visit from(AddVisitCommand command) {
        return Visit.builder()
                .startDateTime(command.getStartDateTime())
                .endDateTime(command.getEndDateTime())
                .build();
    }
}

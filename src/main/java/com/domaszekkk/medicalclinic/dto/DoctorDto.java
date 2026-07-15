package com.domaszekkk.medicalclinic.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private Long userId;
    private List<FacilityDto> facilities;
}
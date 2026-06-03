package com.domaszekkk.medicalclinic.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String specialization;
    private List<FacilityDto> facilities;
}
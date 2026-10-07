package com.example.medicalclinic;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateDoctorRequest {
    private String firstName;
    private String lastName;
    private String specialization;
}

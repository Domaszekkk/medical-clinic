package com.example.medicalclinic;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddDoctorCommand {
    private String firstName;
    private String lastName;
    private String specialization;
    private Long userId;
}

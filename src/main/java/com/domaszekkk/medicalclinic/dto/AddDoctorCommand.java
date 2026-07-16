package com.domaszekkk.medicalclinic.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddDoctorCommand {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String specialization;
}

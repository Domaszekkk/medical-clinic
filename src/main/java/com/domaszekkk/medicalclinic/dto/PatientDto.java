package com.domaszekkk.medicalclinic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientDto {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
package com.domaszekkk.medicalclinic.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddUserCommand  {
    private String email;
    private String password;
}
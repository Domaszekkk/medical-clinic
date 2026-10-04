package com.example.medicalclinic;

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
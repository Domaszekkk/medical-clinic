package com.example.medicalclinic;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long id;
    private String email;
    private String password;

    public static User from(AddUserCommand command) {
        return User.builder()
                .email(command.getEmail())
                .password(command.getPassword())
                .build();
    }

    public void updateFrom(UpdateUserCommand command) {
        this.email = command.getEmail();
    }
}



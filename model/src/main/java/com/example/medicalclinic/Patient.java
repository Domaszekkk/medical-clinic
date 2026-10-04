package com.example.medicalclinic;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient {
    private Long id;
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;
    private User user;

    public static Patient from(AddPatientCommand command, User user) {
        return Patient.builder()
                .idCardNo(command.getIdCardNo())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .phoneNumber(command.getPhoneNumber())
                .birthday(command.getBirthday())
                .user(user)
                .build();
    }

    public void updateFrom(UpdatePatientCommand command) {
        this.idCardNo = command.getIdCardNo();
        this.firstName = command.getFirstName();
        this.lastName = command.getLastName();
        this.phoneNumber = command.getPhoneNumber();
        this.birthday = command.getBirthday();
    }
}

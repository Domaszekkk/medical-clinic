package com.example.medicalclinic;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private User user;

    @Builder.Default
    private List<Facility> facilities = new ArrayList<>();

    public static Doctor from(AddDoctorCommand command) {
        return Doctor.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .specialization(command.getSpecialization())
                .facilities(new ArrayList<>())
                .build();
    }

    public void update(UpdateDoctorRequest request) {
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
        this.specialization = request.getSpecialization();
    }
}

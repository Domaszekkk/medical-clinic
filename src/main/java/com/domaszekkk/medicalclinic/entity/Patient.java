package com.domaszekkk.medicalclinic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "patient")
    private List<Visit> visits;

    public void update(Patient updatedPatient) {
        this.firstName = updatedPatient.getFirstName();
        this.lastName = updatedPatient.getLastName();
        this.phoneNumber = updatedPatient.getPhoneNumber();
        this.birthday = updatedPatient.getBirthday();
        this.idCardNo = updatedPatient.getIdCardNo();
    }
}
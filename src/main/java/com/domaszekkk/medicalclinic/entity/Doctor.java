package com.domaszekkk.medicalclinic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String specialization;

    @ManyToMany
    @JoinTable(name = "doctor_facility",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private List<Facility> facilities;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits;

    public void update(Doctor updatedDoctor) {
        this.email = updatedDoctor.getEmail();
        this.password = updatedDoctor.getPassword();
        this.firstName = updatedDoctor.getFirstName();
        this.lastName = updatedDoctor.getLastName();
        this.specialization = updatedDoctor.getSpecialization();
    }
}
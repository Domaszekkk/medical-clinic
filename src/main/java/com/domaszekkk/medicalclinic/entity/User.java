package com.domaszekkk.medicalclinic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    @OneToMany(mappedBy = "user")
    private List<Patient> patients;

    public void update(User updatedUser) {
        this.email = updatedUser.getEmail();
        this.password = updatedUser.getPassword();
    }
}
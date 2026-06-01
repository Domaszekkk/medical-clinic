package com.domaszekkk.medicalclinic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @OneToOne(mappedBy = "user")
    private Patient patient;

    public void update(User updatedUser) {
        this.email = updatedUser.getEmail();
        this.password = updatedUser.getPassword();
    }
}
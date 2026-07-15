package com.domaszekkk.medicalclinic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facilities")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private String buildingNumber;

    @ManyToMany(mappedBy = "facilities")
    @Builder.Default
    private List<Doctor> doctors = new ArrayList<>();

    public void update(Facility updatedFacility) {
        this.name = updatedFacility.getName();
        this.city = updatedFacility.getCity();
        this.zipCode = updatedFacility.getZipCode();
        this.street = updatedFacility.getStreet();
        this.buildingNumber = updatedFacility.getBuildingNumber();
    }
}

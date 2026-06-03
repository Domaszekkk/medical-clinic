package com.domaszekkk.medicalclinic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "facilities")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private String buildingNumber;

    @ManyToMany(mappedBy = "facilities")
    private List<Doctor> doctors;

    public void update(Facility updatedFacility) {
        this.name = updatedFacility.getName();
        this.city = updatedFacility.getCity();
        this.zipCode = updatedFacility.getZipCode();
        this.street = updatedFacility.getStreet();
        this.buildingNumber = updatedFacility.getBuildingNumber();
    }
}

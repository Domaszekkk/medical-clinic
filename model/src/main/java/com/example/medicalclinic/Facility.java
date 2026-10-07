package com.example.medicalclinic;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Facility {
    private Long id;
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private String buildingNumber;

    public static Facility from(AddFacilityCommand command) {
        return Facility.builder()
                .name(command.getName())
                .city(command.getCity())
                .zipCode(command.getZipCode())
                .street(command.getStreet())
                .buildingNumber(command.getBuildingNumber())
                .build();
    }

    public void update(AddFacilityCommand command) {
        this.name = command.getName();
        this.city = command.getCity();
        this.zipCode = command.getZipCode();
        this.street = command.getStreet();
        this.buildingNumber = command.getBuildingNumber();
    }
}

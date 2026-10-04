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

}

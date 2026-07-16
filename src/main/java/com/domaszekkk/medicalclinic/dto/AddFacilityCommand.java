package com.domaszekkk.medicalclinic.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddFacilityCommand {
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private String buildingNumber;
}

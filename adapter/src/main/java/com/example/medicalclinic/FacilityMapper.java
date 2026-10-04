package com.example.medicalclinic;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    FacilityDto toDto(Facility facility);

    FacilityEntity toEntity(Facility facility);

    Facility toModel(FacilityEntity entity);
}

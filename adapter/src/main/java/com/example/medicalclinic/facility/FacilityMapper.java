package com.example.medicalclinic.facility;

import com.example.medicalclinic.Facility;
import com.example.medicalclinic.FacilityDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    FacilityDto toDto(Facility facility);

    FacilityEntity toEntity(Facility facility);

    Facility toModel(FacilityEntity entity);
}

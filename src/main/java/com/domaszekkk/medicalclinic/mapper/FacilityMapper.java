package com.domaszekkk.medicalclinic.mapper;

import com.domaszekkk.medicalclinic.dto.AddFacilityCommand;
import com.domaszekkk.medicalclinic.dto.FacilityDto;
import com.domaszekkk.medicalclinic.entity.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    FacilityDto mapToDto(Facility facility);

    List<FacilityDto> mapToDtoList(List<Facility> facilities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    Facility mapToEntity(AddFacilityCommand command);
}

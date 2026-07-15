package com.domaszekkk.medicalclinic.mapper;

import com.domaszekkk.medicalclinic.dto.AddDoctorCommand;
import com.domaszekkk.medicalclinic.dto.DoctorDto;
import com.domaszekkk.medicalclinic.dto.UpdateDoctorRequest;
import com.domaszekkk.medicalclinic.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", uses = FacilityMapper.class)
public interface DoctorMapper {

    @Mapping(target = "userId", source = "user.id")
    DoctorDto mapToDto(Doctor doctor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    @Mapping(target = "visits", ignore = true)
    Doctor mapToEntity(AddDoctorCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    @Mapping(target = "visits", ignore = true)
    void updateDoctorFromRequest(UpdateDoctorRequest request, @MappingTarget Doctor doctor);
}
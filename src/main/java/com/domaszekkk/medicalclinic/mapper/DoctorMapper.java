package com.domaszekkk.medicalclinic.mapper;

import com.domaszekkk.medicalclinic.dto.AddDoctorCommand;
import com.domaszekkk.medicalclinic.dto.DoctorDto;
import com.domaszekkk.medicalclinic.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = FacilityMapper.class)
public interface DoctorMapper {
    DoctorDto mapToDto(Doctor doctor);

    List<DoctorDto> mapToDtoList(List<Doctor> doctors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    Doctor mapToEntity(AddDoctorCommand command);
}
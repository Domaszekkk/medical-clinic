package com.example.medicalclinic.doctor;

import com.example.medicalclinic.Doctor;
import com.example.medicalclinic.DoctorDto;
import com.example.medicalclinic.facility.FacilityMapper;
import com.example.medicalclinic.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, FacilityMapper.class})
public interface DoctorMapper {

    @Mapping(target = "userId", source = "user.id")
    DoctorDto toDto(Doctor doctor);

    @Mapping(target = "visits", ignore = true)
    DoctorEntity toEntity(Doctor doctor);

    Doctor toModel(DoctorEntity entity);
}

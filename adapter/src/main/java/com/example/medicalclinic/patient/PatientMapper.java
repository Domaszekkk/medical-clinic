package com.example.medicalclinic.patient;

import com.example.medicalclinic.Patient;
import com.example.medicalclinic.PatientDto;
import com.example.medicalclinic.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PatientMapper {

    @Mapping(target = "userId", source = "user.id")
    PatientDto toDto(Patient patient);

    @Mapping(target = "visits", ignore = true)
    PatientEntity toEntity(Patient patient);

    Patient toModel(PatientEntity entity);
}

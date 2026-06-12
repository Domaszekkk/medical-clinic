package com.domaszekkk.medicalclinic.mapper;

import com.domaszekkk.medicalclinic.dto.AddPatientCommand;
import com.domaszekkk.medicalclinic.dto.PatientDto;
import com.domaszekkk.medicalclinic.dto.UpdatePatientRequest;
import com.domaszekkk.medicalclinic.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "userId", source = "user.id")
    PatientDto mapToDto(Patient patient);

    List<PatientDto> mapToDtoList(List<Patient> patients);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "visits", ignore = true)
    Patient mapToEntity(AddPatientCommand request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "visits", ignore = true)
    Patient mapToEntity(UpdatePatientRequest request);
}
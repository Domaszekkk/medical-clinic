package com.domaszekkk.medicalclinic.mapper;

import com.domaszekkk.medicalclinic.dto.AddPatientRequest;
import com.domaszekkk.medicalclinic.dto.PatientDto;
import com.domaszekkk.medicalclinic.dto.UpdatePatientRequest;
import com.domaszekkk.medicalclinic.entity.Patient;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDto mapToDto(Patient patient);
    List<PatientDto> mapToDtoList(List<Patient> patients);
    Patient mapToEntity(AddPatientRequest request);
    Patient mapToEntity(UpdatePatientRequest request);
}
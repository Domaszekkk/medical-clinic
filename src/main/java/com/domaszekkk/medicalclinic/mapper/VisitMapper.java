package com.domaszekkk.medicalclinic.mapper;

import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.VisitDto;
import com.domaszekkk.medicalclinic.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    VisitDto mapToDto(Visit visit);

    List<VisitDto> mapToDtoList(List<Visit> visits);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    Visit mapToEntity(AddVisitCommand command);
}

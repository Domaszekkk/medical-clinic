package com.example.medicalclinic.visit;

import com.example.medicalclinic.Visit;
import com.example.medicalclinic.VisitDto;
import com.example.medicalclinic.doctor.DoctorMapper;
import com.example.medicalclinic.facility.FacilityMapper;
import com.example.medicalclinic.patient.PatientMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DoctorMapper.class, PatientMapper.class, FacilityMapper.class})
public interface VisitMapper {

    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "facilityId", source = "facility.id")
    VisitDto toDto(Visit visit);

    VisitEntity toEntity(Visit visit);

    Visit toModel(VisitEntity entity);
}
